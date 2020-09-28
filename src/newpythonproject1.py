# To change this license header, choose License Headers in Project Properties.
# To change this template file, choose Tools | Templates
# and open the template in the editor.
# Lion Optimisation Algorithm implementation
# Natural computing assignment
# Python3.6.6
# Main LOA engine

# library importspip
import sys, time
import numpy as np

# Lion Optimisation Algorithm implementation
# Natural computing assignment
# Python3.6.6
# Auxiliary script for main LOA engine

# library imports
import random
import copy
#import scipy.spatial.distance as sci


''' initialises a population of lions based on the parameters specified
 and partitions them into the pride and nomad structures '''

def generateGroups(nPop, sexRate, prideNo, percentNomad, upper_limit, lower_limit, dim, evaluation, o):

    # expected number of lions in each structure
    nomadPop = int(round(nPop * percentNomad, 0))
    pridePop = nPop - nomadPop


    ''' setting up gender distribution for lions '''
    # bit array to determine whether lion is a male
    # eg. [0,1,0,0,0,1] indicates the second and last lion to be males
    # the rest being females
    malePrideIndicies = np.zeros(pridePop)
    maleNomadIndicies = np.zeros(nomadPop)

    # the number of expected males in the population of prides and nomads
    noPrideMales = int(round(pridePop * (1 - sexRate), 0))
    noNomadMales = int(round(nomadPop * sexRate, 0))

    # generate bit array with correct no of males
    for i in range(noPrideMales):
        malePrideIndicies[i] = 1

    for i in range(noNomadMales):
        maleNomadIndicies[i] = 1

    # mix up the distribution of males a bit
    random.shuffle(maleNomadIndicies)
    random.shuffle(malePrideIndicies)


    ''' generating lions into the structures '''
    # init arrays of nomad and pride lions

    nomadLionsArray = np.array([Lion() for i in range(nomadPop)])
    prideLionsArray = np.array([Lion() for i in range(pridePop)])


    # set attributes for nomad lions
    for i in range(nomadPop):

        nomadLionsArray[i].isNomad = True
        nomadLionsArray[i].evaluation = evaluation
        nomadLionsArray[i].isMature = True
        nomadLionsArray[i].o = o

        # set gender of nomad lions
        if maleNomadIndicies[i] == 1:
            nomadLionsArray[i].isMale = True
        else:
            nomadLionsArray[i].isMale = False

        # initialize lion positions
        nomadLionsArray[i].x = np.random.uniform(lower_limit, upper_limit, (1, dim))
        nomadLionsArray[i].bestVisitedPosition = nomadLionsArray[i].x


    # init array of prideNo pride groups
    prideArray = np.array([Group() for i in range(prideNo)])

    # counter to ensure that there is at least one male in each pride (prevents errors)
    j = 0
    for i in range(pridePop):

        prideLionsArray[i].isNomad = False
        prideLionsArray[i].o = o
        prideLionsArray[i].isMature = True
        prideLionsArray[i].evaluation = evaluation

        # set gender of pride lions
        if malePrideIndicies[i] == 1:
            prideLionsArray[i].isMale = True

            # ensure each pride has two male lions
            if j in range(2 * prideNo):
                prideIndex = j % 4
                j += 1
        else:
            prideLionsArray[i].isMale = False

        # initialize lion positions
        
        prideLionsArray[i].x = np.random.uniform(lower_limit, upper_limit, (1, dim))
        prideLionsArray[i].bestVisitedPosition = prideLionsArray[i].x


        ''' assigning each pride lion to a pride '''
        # index of pride to assign lion
        # eg for 4 prides, number is 0,1,2,3
        if j not in range(2 * prideNo) or malePrideIndicies[i] != 1:
            prideIndex = np.random.randint(0, prideNo)

        prideArray[prideIndex].lionArray = np.append(prideArray[prideIndex].lionArray, prideLionsArray[i])


    return prideArray, nomadLionsArray


''' females go hunting to explore the search space by attacking hypothetical prey '''
''' Opposition-Based-Learning implementation '''
# under step 3

def hunting(prideArray, upper_limit, lower_limit):

    for pride in prideArray:
        # assign lion to a hunting group
        for lion in pride.lionArray:
            # put lion back into search area
            for i, j in enumerate(lion.x[0]):
                if j > upper_limit:
                    lion.x[0][i] = upper_limit
                if j < lower_limit:
                    lion.x[0][i] = lower_limit

                   
            # 0 is not in group, 1, 2, 3 correspond to respective hunting groups
            if lion.isMale == True:        # male lions do not hunt
                lion.huntingGroup = 0
            else:
                lion.huntingGroup = np.random.randint(0, 4)
        
        huntingGroup1Fitness = np.sum([lion.getCurrentPositionScore()
                                        for lion in pride.lionArray if lion.huntingGroup == 1])
        huntingGroup2Fitness = np.sum([lion.getCurrentPositionScore()
                                        for lion in pride.lionArray if lion.huntingGroup == 2])
        huntingGroup3Fitness = np.sum([lion.getCurrentPositionScore()
                                        for lion in pride.lionArray if lion.huntingGroup == 3])
        
        
        ''' set position of prey to average of hunter positions '''
        preyPosition = np.zeros(len(pride.lionArray[0].x))      # initialize prey position

        hunterLionNumber = 0                                    # count the number of hunter lions in the pride

        
        if not(lion.huntingGroup == 0):
            
            preyPosition = np.add(preyPosition, lion.x)     # add the positions within each basis
            hunterLionNumber += 1

        # get the average position of the hunter lions
        preyPosition /= hunterLionNumber

        # hunting females all attack the prey
        # first set center group to be the group with max fitness, left and right groups are two
        # lower fitness groups
        fitnesses = [huntingGroup1Fitness, huntingGroup2Fitness, huntingGroup3Fitness]
        centre = np.argsort(fitnesses)[0]
        right = np.argsort(fitnesses)[1]
        left = np.argsort(fitnesses)[2]

        for lion in pride.lionArray:
            
            # Change of lion position depends if they are in left or right group
            if lion.huntingGroup == centre:
                # move the lion according to strategy provided by the paper
                if lion.evaluation(preyPosition, lion.o) > lion.getCurrentPositionScore():
                    lion.x = np.random.uniform(lion.x, preyPosition)

                if lion.evaluation(preyPosition, lion.o) < lion.getCurrentPositionScore():
                    lion.x = np.random.uniform(preyPosition, lion.x)
            
            if (lion.huntingGroup == right) or lion.huntingGroup == left:
                ## move lion according to strategy provided by the paper
                if lion.evaluation(2 * preyPosition - lion.x, lion.o) < lion.evaluation(preyPosition, lion.o):
                    lion.x = np.random.uniform(2 * preyPosition - lion.x, preyPosition)

                if lion.evaluation(2 * preyPosition - lion.x, lion.o) > lion.evaluation(preyPosition, lion.o):
                    lion.x = np.random.uniform(preyPosition, 2 * preyPosition - lion.x)
                
            # If lion's position is improved, update it's best visited position and score
            if lion.getBestVisitedPositionScore() > lion.getCurrentPositionScore():
                # get the improvement percentage
                improvement_percentage = lion.getBestVisitedPositionScore() / lion.getCurrentPositionScore()
                lion.bestVisitedPosition = lion.x
                # change the position of the prey according to Opposition Based Learning
                preyPosition = preyPosition +( np.random.uniform(0, 1) * improvement_percentage * (preyPosition - lion.x))


    return prideArray


"""
    The input should be a pride that has recently gone hunting
    this funciton will take the female lions that have not gone hunting
    and based on a tournament selection strategy, moves them into the direction
    of a better location
"""
def moveToSafePlace(prideArray, upper_limit, lower_limit, dim):

    for pride in prideArray:

        # the number of lions that have improved in the previous iteration
        numberImprovedLions = sum([1 for lion in pride.lionArray if lion.bestScoreHistory[-1] < lion.bestScoreHistory[-2]])

        # compute tournament size
        tournamentsize = max([2, int(np.ceil(numberImprovedLions / 2))])

        # best visited positions and their scores in one list
        bestVisitedPositions = [(lion.bestVisitedPosition, lion.getBestVisitedPositionScore()) for lion in pride.lionArray]

        for lion in pride.lionArray:
            # if the female is not hunting
            if lion.huntingGroup == 0 and lion.isMale == False:

                # tournament selection
                tournamentSelection = random.sample(bestVisitedPositions, tournamentsize)
                # winner has lowest fitness value
                winner = min(tournamentSelection,key=lambda item:item[1])[0]

                R1 = (winner.T).reshape(len(winner.T),)
                startposition = (lion.x.T).reshape(len(lion.x.T),)
                R1 = R1 - startposition

                # some parameters for moving the female non-hunting lion
                # distance is a percetage of the maximum distance in the search space
                D = np.linalg.norm(R1 - lion.x) \
                                / np.linalg.norm(upper_limit - lower_limit)

                # create random orthonormal vector to R1
                R2 = np.random.randn(len(R1.T))
                if np.linalg.norm(R1) != 0:
                    R2 -= R2.dot(R1) * R1 / np.linalg.norm(R1)**2
                else:
                    # if R1 is the zero, generate 0 vector with random 1
                    R2 = np.zeros((len(R1)))[np.random.randint(0, len(R1))] = 1

                theta = np.random.uniform(-np.pi/6, np.pi/6)

                # move the female lion according to the formula provided in the paper
                lion.x = lion.x + 2 * D * np.random.uniform(0,1) * R1 + \
                          np.random.uniform(-1, 1) * np.tan(theta) * R2 * D

    return prideArray


''' remaing males in pride explore best visited positions '''
# step 3
def pridesRoam(prideArray, roamingPercent, upper_limit, lower_limit, dim):

    for pride in prideArray:

        # move all male lions towards each selected position
        for lion in pride.lionArray:
            if lion.isMale == True:

                # territory containing best visited locations of all lions in the pride
                territory = [(lion.bestVisitedPosition, lion.getBestVisitedPositionScore()) for lion in pride.lionArray]

                #%roamingPercent of the territory is randomly selected for roaming
                selected = random.sample(territory, int(np.ceil(len(territory) * roamingPercent)))
                for place in selected:
                    angle = np.random.uniform(-np.pi/6, np.pi/6)
                    distance = np.linalg.norm(place[0] - lion.x) \
                                    / np.linalg.norm(upper_limit - lower_limit)
                    distance = 1
                    step = np.random.uniform(0, 0.2 * distance)
                    lion.x += step * (place[0] - lion.x) * np.tan(angle)

                    if lion.getCurrentPositionScore() < lion.getBestVisitedPositionScore():
                        lion.bestVisitedPosition = lion.x

    return prideArray



def prideMating(prideArray, mateProb, mutateProb, lower_limit, upper_limit, o):
    for pride in prideArray:
        for lion in pride.lionArray:
            # only mate if female and with mating probability
            if (lion.isMale != True) and (random.random() < mateProb):
                # select number of mates for female lion
                males = [l for l in pride.lionArray if l.isMale == True]
                if len(males) >= 2:
                    numberMates = random.randint(1, 2)
                elif len(males) == 0:
                    numberMates = 0
                else:
                    numberMates = random.randint(1, len(males))

                #randomly select males in the population
                maleMates = random.sample(males, numberMates)

                # female mates with all the selected male mates from the pride
                # create cubs
                cub1, cub2 = matingOperator(lion.x, np.array([male.x for male in maleMates]),
                                            mutateProb, lower_limit, upper_limit)
                # initialize variables too
                cub1.evaluation, cub2.evaluation = lion.evaluation, lion.evaluation
                cub1.isNomad, cub2.isNomad = False, False
                cub1.o, cub2.o = o, o

                # add cubs become mature and are added to the array of nomad lions
                cub1.isMature, cub2.isMature = True, True
                pride.lionArray = np.append(pride.lionArray, cub1)
                pride.lionArray = np.append(pride.lionArray, cub2)

    return prideArray

"""
Weakest males in a pride are kicked out and become nomads
the number of males that are kicked out is proportional to how
many there are supposed to be there
"""
def prideDefense(prideArray, nomadLionsArray, sexRate, nPop, percentNomad, prideNo):

    maxMalePrideNo = np.ceil(nPop * (1 - percentNomad) * (1 - sexRate) * (1 / prideNo))

    for pride in prideArray:
        # create lists with female and male lions
        prideFemales = [lion for lion in pride.lionArray if lion.isMale == False]
        prideMales = [lion for lion in pride.lionArray if lion.isMale == True]

        #sort male lions based on their fitness
        prideMales = sorted(prideMales, key=lambda lion: lion.getBestVisitedPositionScore()[0], reverse=False)

        ## weak lions become nomads
        new_nomads = []
        while len(prideMales) > maxMalePrideNo:
            prideMales[-1].isNomad = True
            new_nomads = np.append(new_nomads, prideMales[-1])
            del prideMales[-1]

        #collect surviving lions together
        remainingPrideLions = np.concatenate((prideFemales, prideMales))

        pride.lionArray = remainingPrideLions
        nomadLionsArray = np.append(nomadLionsArray, new_nomads)

    return prideArray, nomadLionsArray


# nomad lions moving randomly in the search space
# under step 4
def nomadsRoam(nomadLionsArray, lower_limit, upper_limit, dim):

    for lion in nomadLionsArray:

        # best position evaluation of all nomad lions
        bestPositionScore = np.min([l.getCurrentPositionScore() for l in nomadLionsArray])

        # the max threshold number in order for the lion to roam
        thresholdRoamingProbability = 0.1 + np.min([0.5, (lion.getCurrentPositionScore()[0] - bestPositionScore) / bestPositionScore])

        # move lion if not greater than the threshold
        if not(random.random() > thresholdRoamingProbability):
             lion.x = np.random.uniform(lower_limit, upper_limit, (1, dim))

        # update the best visited position if better than current
        if lion.getBestVisitedPositionScore() > lion.getCurrentPositionScore():
            lion.bestVisitedPosition = lion.x


    return nomadLionsArray


# female nomads mate with one of best male nomads and cubs become mature
# under step 4
def mateNomads(nomadLionsArray, mateProb, mutateProb, lower_limit, upper_limit, o):

    # for each lion
    for lion in nomadLionsArray:

        # skip if lion is not female AND only mate with probability ~ mateProb
        if (lion.isMale != True) and (random.random() < mateProb):

            # randomly select a male lion for breeding
            maleMate = random.sample([l for l in nomadLionsArray if l.isMale == True], 1)[0]

            # get cubs
            cub1, cub2 = matingOperator(lion.x, np.array([maleMate.x]), mutateProb, lower_limit, upper_limit)
            cub1.evaluation, cub2.evaluation = lion.evaluation, lion.evaluation
            cub1.isNomad, cub2.isNomad = True, True

            # add cubs become mature and are added to the array of nomad lions
            cub1.isMature, cub2.isMature = True, True
            cub1.o, cub2.o = o, o
            nomadLionsArray = np.append(nomadLionsArray, cub1)
            nomadLionsArray = np.append(nomadLionsArray, cub2)

    return nomadLionsArray



# male nomad lions attack a resident male of a pride
# resident males are places depending on which lion is stronger
# under step 4
def nomadsAttackPride(prideArray, nomadLionsArray):

    for nomadInd in range(len(nomadLionsArray)):

        # skip if nomad is female
        if nomadLionsArray[nomadInd].isMale == False:
            continue

        # generate binary array with length of number of prides
        # ie for 4 prides: [0,1,1,0] means attack the prides 2 and 3 only
        pridesToAttack = [random.randint(0, 1) for i in range(len(prideArray))]

        # for each pride
        for prideInd in range(len(prideArray)):

            # attack pride
            if pridesToAttack[prideInd] == 1:

                # get resident lion from the pride
                maleIndex = [mInd for mInd in range(len(prideArray[prideInd].lionArray)) if prideArray[prideInd].lionArray[mInd].isMale == True][0]
                residentLion = prideArray[prideInd].lionArray[maleIndex]

                # get the nomad lion to contest
                nomadLion = nomadLionsArray[nomadInd]

                # if the nomad is stronger than the resident
                if nomadLion.getCurrentPositionScore() > residentLion.getCurrentPositionScore():

                    # create a temporary resident lion object copy
                    residentLionCopy = copy.deepcopy(residentLion)
                    nomadLionCopy = copy.deepcopy(nomadLion)

                    # replace resident with nomad
                    residentLion = nomadLionCopy
                    residentLion.isNomad = False

                    # replace nomad with copy of resident
                    nomadLion = residentLionCopy
                    nomadLion.isNomad = True


    return prideArray, nomadLionsArray


# some females migrate from the pride and become nomad
# step 5
def migrateFemaleFromPride(prideArray, nomadLionsArray, migrateRate, sexRate, nPop, prideNo, percentNomad):

    # calculate the maximum number of females permitted based on the sex rate and pride population
    maxFemaleNo = np.ceil(nPop * (1 - percentNomad) * (sexRate) * (1 / prideNo))

    # for each pride
    for pride in prideArray:

        # if there are more females in the pride than permitted, remove them to become nomads
        while maxFemaleNo < len([l for l in pride.lionArray if l.isMale == False]):

            # get the indicies of the female lions in the pride's array of lions
            indiciesFemaleLions = [i for i in range(len(pride.lionArray)) if pride.lionArray[i].isMale == False]

            # get index of female to remove from the array
            indToKick = random.sample(indiciesFemaleLions, 1)[0]

            # get the lion object of female to remove
            femaleToKick = copy.deepcopy(pride.lionArray[indToKick])

            # kick the female lion from the pride
            pride.lionArray = np.delete(pride.lionArray, indToKick)

            # add female lion to the nomad array
            femaleToKick.isNomad = True
            nomadLionsArray = np.append(nomadLionsArray, femaleToKick)


        # remaining females may also decide to migrate with chance migrateRate
        ind = 0             # iterate through the lions in the pride
        while ind < len(pride.lionArray):

            # for each lion that is female AND with probability ~ migrateRate
            if (pride.lionArray[ind].isMale == False) and (random.random() < migrateRate):

                # get the lion object of female to migrate
                femaleToMigrate = copy.deepcopy(pride.lionArray[ind])

                # migrate the female lion from the pride
                pride.lionArray = np.delete(pride.lionArray, ind)

                # add female lion to the nomad array
                femaleToMigrate.isNomad = True
                nomadLionsArray = np.append(nomadLionsArray, femaleToMigrate)

                # increment number of females that have migrated
                pride.migratedFemaleNo += 1

                # update for next iteration if number of lions in pride has decreased
                ind -= 1

            # update iteration index
            ind += 1


    return prideArray, nomadLionsArray



# returns a set of the indicies of prides which are not full because females have migrated
# ie (1,3) would indicate the second and fourth pride can have more female lions to replace migrated ones
# used for step6
def nonFullPrides(prideArray):

    nonFullPrideIndicies = set()
    for i in range(len(prideArray)):
        if not(prideArray[i].migratedFemaleNo == 0):
            nonFullPrideIndicies.add(i)

    return nonFullPrideIndicies


# move some female nomad lions to a pride which has spare capacity
# prides have spare capacity as a result of prior female migration
# remove the weakest lions to remain consistent with permitted number for each gender
def step6(prideArray, nomadLionsArray, nPop, sexRate, percentNomad, prideNo):

    # list of male and female lions sorted by strength
    maleNomads = [lion for lion in nomadLionsArray if lion.isMale == True]
    maleNomads = sorted(maleNomads, key=lambda lion: lion.getCurrentPositionScore()[0], reverse=False)

    femaleNomads = [lion for lion in nomadLionsArray if lion.isMale == False]
    femaleNomads = sorted(femaleNomads, key=lambda lion: lion.getCurrentPositionScore()[0], reverse=False)

    ''' adding fittest female nomads to a pride with spare capacity '''
    # while there are still some empty places in a pride due to migration
    # add a female lion to the pride based on fitness
    while len(nonFullPrides(prideArray)) != 0:

        # get indicies of prides which have spare capacity
        nonFullPrideIndicies = nonFullPrides(prideArray)

        # select at random a pride to add a lion
        prideIndex = random.sample(nonFullPrideIndicies, 1)[0]

        # add fittest female to the pride
        femaleNomads[0].isNomad = False
        prideArray[prideIndex].lionArray = np.append(prideArray[prideIndex].lionArray, femaleNomads[0])

        # update array and decrement the number of spaces left in the pride object
        del femaleNomads[0]
        prideArray[prideIndex].migratedFemaleNo -= 1


    ''' removing the least fit nomads '''
    # to remain consistent max number of each gender in nomad population
    maxMaleNomadNo = nPop * percentNomad * sexRate
    maxFemaleNomadNo = nPop * percentNomad * (1 - sexRate)

    # if number of male nomads is greater than that permitted
    # remove the least fittest
    while len(maleNomads) > maxMaleNomadNo:
        del maleNomads[-1]

    while len(femaleNomads) > maxFemaleNomadNo:
        del femaleNomads[-1]

    # collect surviving lions together
    remainingNomadLions = np.concatenate((femaleNomads, maleNomads))


    # """removing least fit pride members"""
    # maxMalePrideNo = np.ceil(nPop * (1 - percentNomad) * (1 - sexRate) * (1/prideNo))
    # maxFemalePrideNo = np.ceil(nPop * (1 - percentNomad) * sexRate * (1/prideNo))
    #
    # for pride in prideArray:
    #     # create lists with female and male lions
    #     prideMales = [lion for lion in pride.lionArray if lion.isMale == True]
    #     prideFemales = [lion for lion in pride.lionArray if lion.isMale == False]
    #     #sort lions based on their fitness
    #     prideMales = sorted(prideMales, key=lambda lion: lion.getBestVisitedPositionScore()[0]
    #                          , reverse=False)
    #     prideFemales = sorted(prideFemales, key=lambda lion: lion.getBestVisitedPositionScore()[0]
    #                           , reverse=False)
    #
    #     ## kill weak lions
    #     while len(prideMales) > maxMalePrideNo:
    #         del prideMales[-1]
    #
    #     while len(prideFemales) > maxFemalePrideNo:
    #         del prideFemales[-1]
    #
    #     #collect surviving lions together
    #     remainingPrideLions = np.concatenate((prideFemales, prideMales))
    #
    #     pride.lionArray = remainingPrideLions


    return prideArray, remainingNomadLions



# mating operator for a female to breed with one or more male lion
# femalePos is lion.x - the position of the female
# malesPos is an array of [lion.x] - the positions of the males in a np.array
# returns two offspring Lion objects following the procedure in the paper
# used for steps 3 and 4
def matingOperator(femalePos, malesPosAr, mutateProb, lower_limit, upper_limit):

    # random gaussian var as per the paper to normalise "crossover"
    beta = np.random.normal(0.5, 0.1)

    # number of males in the mating
    maleNo = len(malesPosAr)

    # sum up over each basis the positions of all males
    # eg. [1,1,1] + [3,4,6] => [4,5,7]
    malePositionSum = np.zeros(len(femalePos))                  # initialize positions

    for malePos in malesPosAr:
        malePositionSum = np.add(malePositionSum, malePos)      # add the positions within each basis

    # get the average position of the male lions
    malePositionAve = malePositionSum / maleNo

    # generate offspring position vectors
    offspringVec1 = beta * femalePos + (1 - beta) * malePositionAve
    offspringVec2 = (1 - beta) * femalePos + beta * malePositionAve

    # mutate the position vectors
    for basis in range(len(offspringVec1)):

        # mutate each basis of first offspring vector with probability ~ mutateProb
        if random.random() < mutateProb:
            offspringVec1[basis] = np.random.uniform(lower_limit, upper_limit)         # change to a random number in the search space

        # mutate each basis of second offspring vector with probability ~ mutateProb
        if random.random() < mutateProb:
            offspringVec2[basis] = np.random.uniform(lower_limit, upper_limit)         # change to a random number in the search space


    # put these vectors into lion objects
    cub1 = Lion()
    cub1.x = offspringVec1
    cub1.isMale = (beta >= 0.5)       # randomly set gender of cub
    cub1.bestVisitedPosition = offspringVec1
    cub1.isMature = False
    #cub1.evaluation = None           # VAR MUST BE SET ONCE THIS FUNCTION HAS RETURNED!!!
    #cub1.isNomad = None              # VAR MUST BE SET ONCE THIS FUNCTION HAS RETURNED!!!

    cub2 = Lion()
    cub2.x = offspringVec2
    cub2.isMale = not(cub1.isMale)    # randomly set gender of cub
    cub2.bestVisitedPosition = offspringVec2
    cub2.isMature = False
    #cub2.evaluation = None           # VAR MUST BE SET ONCE THIS FUNCTION HAS RETURNED!!!
    #cub2.isNomad = None              # VAR MUST BE SET ONCE THIS FUNCTION HAS RETURNED!!!


    return cub1, cub2



def updateBestScoreList(prideArray, nomadLionsArray):
    """
    function to update the list of best scores obtained by each lion.
    This is done for tournament selection in hunting
    """
    for pride in prideArray:
        for lion in pride.lionArray:
            lion.bestScoreHistory.append(lion.getBestVisitedPositionScore())

    for lion in nomadLionsArray:
        lion.bestScoreHistory.append(lion.getBestVisitedPositionScore())

    return prideArray, nomadLionsArray

def getCurrentGlobalBest(prideArray, nomadLionsArray):
    scores = np.array([])
    for pride in prideArray:
        for lion in pride.lionArray:
            scores = np.append(scores, lion.getBestVisitedPositionScore())

    for lion in nomadLionsArray:
            scores = np.append(scores, lion.getBestVisitedPositionScore())

    return np.min(scores)

# represents a pride
class Group:

    def __init__(self):

        self.lionArray = np.array([])
        self.migratedFemaleNo = 0


class Lion:

    def __init__(self):

        self.isMale = None
        self.evaluation = None
        self.bestVisitedPosition = None
        self.isMature = None
        self.isNomad = None
        self.x = None
        self.huntingGroup = None
        self.bestScoreHistory = [np.Infinity]      # keep track of best score found so far for tournament
        self.o = None


    # fitness value of best visited position
    def getBestVisitedPositionScore(self):
        return self.evaluation(self.bestVisitedPosition, self.o)

    # fitness value of current position
    def getCurrentPositionScore(self):
        return self.evaluation(self.x, self.o)

def HC(x):
    sum = 0.0
    for i in range(1, len(x) + 1):
        sum += ((10 ** 6) ** ((i - 1) / (len(x) - 1))) * x[i - 1] ** 2
    return sum


def SHC(x, o):
    F_1 = 100
    return HC((x - o).T) + F_1
  

def LOA():
    # set algorithm params as global variables
    prideNo =4            # number of pride structures
    percentNomad = 0.2     # portion of population to be nomads
    roamingPercent = 0.2
    mutateProb = 0.2
    sexRate = 0.8
    mateProb = 0.3
    migrateRate = 0.4
    nPop = int(sys.argv[1])
    upper_limit = int(sys.argv[2])
    lower_limit = 0
    dim = int(sys.argv[1])
    evaluation = SHC
    #o sets the random rotation of the function
    o = np.random.uniform(-upper_limit,upper_limit, (1, dim))
    maxIterationNo = 15


    ''' steps 1 & 2 '''
    # initialise the populations into structures of prides and nomads
    prideArray, nomadLionsArray = generateGroups(nPop, sexRate, prideNo, percentNomad,upper_limit, lower_limit, dim, evaluation, o)

    global_best = [np.inf]
    # list to track current best value
    track = []
    bestLionsArray=[]
    for it in range(maxIterationNo):

        #start_time = time.time()

        # update the list of best scores obtained for each iteration
        prideArray, nomadLionsArray = updateBestScoreList(prideArray, nomadLionsArray)


        ''' step 3 '''
        prideArray = hunting(prideArray, upper_limit, lower_limit)
        prideArray = moveToSafePlace(prideArray, upper_limit, lower_limit, dim)
        prideArray = pridesRoam(prideArray, roamingPercent, upper_limit, lower_limit, dim)
        prideArray = prideMating(prideArray, mateProb,mutateProb, lower_limit, upper_limit, o)
        prideArray, nomadLionsArray = prideDefense(prideArray, nomadLionsArray,sexRate, nPop, percentNomad, prideNo)


        ''' step 4 '''
        # move nomads about randomly in search space
        nomadLionsArray = nomadsRoam(nomadLionsArray, lower_limit, upper_limit, dim)
        
        # nomads mate
        nomadLionsArray = mateNomads(nomadLionsArray, mateProb, mutateProb, lower_limit, upper_limit, o)
        
        # nomad male randomly attack pride
        prideArray, nomadLionsArray = nomadsAttackPride(prideArray, nomadLionsArray)


        ''' step 5 '''
        # females migrate from a pride and join the nomads with some probability
        prideArray, nomadLionsArray = migrateFemaleFromPride(prideArray, nomadLionsArray, migrateRate, sexRate, nPop, prideNo, percentNomad)


        ''' step 6 '''
        # allocate some female nomad lions to the prides
        # kill off the least fit nomad lions
        prideArray, nomadLionsArray = step6(prideArray, nomadLionsArray, nPop, sexRate, percentNomad, prideNo)

        ''' best score obtained so far '''
        current_best = getCurrentGlobalBest(prideArray, nomadLionsArray)
        track.append(current_best)
        
        if current_best < global_best[-1]:
            # keep track of global best per iteration
            global_best.append(current_best)
            #print("improved score: %.2E" % current_best)
            bestLionsArray.clear()
            for pride in prideArray:
                for lion in pride.lionArray:
                    bestLionsArray.append(lion)
                    
                    
            for lion in nomadLionsArray:
                bestLionsArray.append(lion)
                

        else:
            global_best.append(global_best[-1])

            #elapsed_time = (time.time() - start_time)
        #if it % 100 == 0:
        #print("Finished%", it/maxIterationNo)
        
    minScore=sys.maxsize
    
    for lion in bestLionsArray:
        if(lion.getCurrentPositionScore()<minScore):
              bestLion=lion
    bestPositions=[]
    random.seed(5)
    for i in bestLion.x:
     for j in i:
                
        a=abs(int(j))
        if(a>=upper_limit):
            a=random.randrange(0,10)
        elif(a<lower_limit):
            a=random.randrange(0,10)
        bestPositions.append(a)  
    # reshape the progress in order to save in array in main()
    #print(np.array(global_best).shape)
    return np.reshape(np.array(global_best), (len(global_best),)), np.reshape(np.array(track), (len(track),)),bestPositions

def main():
    runs = 1
    iterations = 15
    # initialize matrix that saves all values
    # each row is one run of the algorithm, containing the progress of global best per iterations
    # each column is the value at each iteration for all the runs
    currentBestCurves = np.zeros((runs, iterations))
    globalBestCurves = np.zeros((runs, iterations + 1))


    bestPositions=[]

    for run in range(runs):
        globalBestCurves[run],  currentBestCurves[run],bestPositions = LOA() 
    for i in bestPositions:
            print(i) 
   
        #if run % 10 == 0:
        #print("number of runs completed = ", run,  "/", runs)

    #np.save("globalBestCurves", globalBestCurves)
    #np.save("currentBestCurves", currentBestCurves)

    #print("COMPLETED")

if __name__ == "__main__":
    main()
