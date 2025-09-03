import pandas as pd
import math
## CS483 Project 1
## Jake Onkka
## Bernoulli Naive Bayes Classifier
## Must run with the zoo.csv file in current directory, outputs csv format to stdout displaying
## the test data and additional columns indicating the classifier's predictions
#read in file and separate training from testing data
data = pd.read_csv('zoo.csv')
train = data.sample(frac = 0.70)
test = data.drop(train.index)


class_count = {}   #count how many in each class
for x in range(1,8):
    animalcount = len(train.query('class_type ==@ x'))  #using @ here let's us use variable in query
    class_count[x] = animalcount
    #print(class_priors[x])

#conditional probability
#p(count|class) + alpha / count(class) + (alpha * D)
def p_conditional(feature, value, class_type):
    alpha = 0.01
    D = 7
    count_class = train[train['class_type'] == class_type].shape[0]
    count_feature_given_class = train[(train['class_type'] == class_type) & (train[feature] == value)].shape[0]
    return (count_feature_given_class + alpha) / (count_class + (alpha * D))

#probability of class
#count(class) / total count
def p_class(class_type):
    #print("Prob of class " + str(class_type))
    numerator = class_count[class_type]
    denominator = len(train)
   # print("num " + str(numerator) + " den " + str(denominator))
    return numerator / denominator

#print first row in csv format
print(f"{'animal_name'},{'hair'},{'feathers'},{'eggs'},"
          f"{'milk'},{'airborne'},{'aquatic'},{'predator'},"
          f"{'toothed'},{'backbone'},{'breathes'},{'venomous'},{'fins'},"
          f"{'legs'},{'tail'},{'domestic'},{'catsize'},{'class_type'},{'predicted'},{'probability'},{'correct?'}")

#calculating bn
for index,row in test.iterrows():   #FOR EVERY ROW
    best_class = None   #highest bn changes class to current class
    actual_class = row['class_type']
    best_probability= -math.inf #highest bn / denom
    denom = 0   #sum of bn for all features, divide by this for probability
    for class_type in range(1,8):   #FOR EVERY CLASS
        nb = math.log2(p_class(class_type)) #default nb with just class prob
        #test every feature except name and class_type for obvious reasons
        for feature in ['hair', 'feathers', 'eggs', 'milk', 'airborne', 'aquatic', 'predator', 'toothed', 'backbone',
                        'breathes', 'venomous', 'fins', 'legs', 'tail', 'domestic', 'catsize']:
            value = row[feature]
            nb += math.log2(p_conditional(feature,value,class_type))    #add the log(p(feature|class))

        denom += nb #total nb


        if nb > best_probability:   #check which class got the highest bn
            best_probability = nb
            best_class = class_type

        #nb = math.pow(2,nb)
        #print("nb " + str(nb) + " bp " + str(best_probability) + " denom " + str(denom))
    #best_probability = math.pow(2,best_probability)
    #denom = math.pow(2,denom)
    best_probability = best_probability / (denom)
    best_probability = math.pow(2,best_probability)
    #nb = best_probability / denom
    #nb = math.pow(2,nb)
    if best_class == actual_class:
        correct = 'CORRECT'
    else:
        correct = 'wrong'
    #output in csv format to stdout
    print(f"{row['animal_name']},{row['hair']},{row['feathers']},{row['eggs']},"
          f"{row['milk']},{row['airborne']},{row['aquatic']},{row['predator']},"
          f"{row['toothed']},{row['backbone']},{row['breathes']},{row['venomous']},{row['fins']},"
          f"{row['legs']},{row['tail']},{row['domestic']},{row['catsize']},{actual_class},{best_class},"
          f"{best_probability},{correct}")