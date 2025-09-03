## CS483 Project 3
## Jake Onkka
## Trees vs Lines
## This is the same as project 2 but instead testing the differences between:
## linear regression, random forests, gradient boosting, and decision trees

import pandas as pd
import numpy as np
from sklearn.linear_model import LinearRegression
from sklearn.metrics import r2_score
from sklearn.pipeline import Pipeline
from sklearn.base import TransformerMixin, BaseEstimator
from sklearn.compose import TransformedTargetRegressor
from sklearn.model_selection import train_test_split
from sklearn.pipeline import Pipeline
from sklearn.model_selection import GridSearchCV
from sklearn.ensemble import RandomForestRegressor
from sklearn.tree import DecisionTreeRegressor
from sklearn.ensemble import GradientBoostingRegressor
data = pd.read_csv('AmesHousing.csv')
data = data.drop(columns = ['Neighborhood']) #dropping neighborhood, cannot use this feature
data.fillna(value=0,inplace=True) #fill all NaN
data = pd.get_dummies(data)
#print(data)
class SelectColumns(BaseEstimator,TransformerMixin):
    def __init__(self,columns):
        self.columns = columns
    def fit(self,xs,ys,**params):
        return self
    def transform(self,xs):
        return xs[self.columns]
regressor = TransformedTargetRegressor(
    LinearRegression(n_jobs = -1),
    func = np.sqrt,
    inverse_func = np.square
)
xs = data.drop(columns = ['SalePrice'])
ys = data['SalePrice']
#train_x, test_x, train_y, test_y = train_test_split(xs,ys,train_size = 0.7)
#pipe.fit(xs,ys)
linearregression_pipe = Pipeline([
    ('column_select', SelectColumns([])),
    ('linear_regression', regressor),
])

linearregression_grid = {'column_select__columns': [

    ['Gr Liv Area', 'Overall Qual', 'Overall Cond', 'Year Built',
     'Kitchen Qual_Ex', 'Kitchen Qual_Gd', 'Kitchen Qual_TA', 'Exter Qual_Ex', 'Exter Qual_Gd', 'Exter Qual_TA',
     'Bsmt Qual_Ex', 'Bsmt Qual_Gd', 'Bsmt Qual_TA',
     'Fireplaces', 'Lot Area', 'MS SubClass', 'Garage Cars'],
],
    'linear_regression': [
        LinearRegression(n_jobs=-1),
        TransformedTargetRegressor(
            LinearRegression(n_jobs=-1),
            func=np.sqrt,
            inverse_func=np.square),
        TransformedTargetRegressor(
            LinearRegression(n_jobs=-1),
            func=np.cbrt,
            inverse_func=lambda y: np.power(y, 3)),
        TransformedTargetRegressor(
            LinearRegression(n_jobs=-1),
            func=np.log,
            inverse_func=np.exp),
    ]
}
randomforest_pipeline = Pipeline([
    ('column_select',SelectColumns([])),
    ('random_forest', RandomForestRegressor())
])

randomforest_grid = {
     'column_select__columns': [

['Gr Liv Area', 'Overall Qual', 'Overall Cond', 'Year Built',
'Kitchen Qual_Ex', 'Kitchen Qual_Gd', 'Kitchen Qual_TA', 'Exter Qual_Ex', 'Exter Qual_Gd', 'Exter Qual_TA', 'Bsmt Qual_Ex', 'Bsmt Qual_Gd', 'Bsmt Qual_TA',
'Fireplaces', 'Lot Area', 'MS SubClass', 'Garage Cars'],
    ],
    'random_forest__n_estimators': [3, 5, 10],  #how many trees in the forest
    'random_forest__max_depth': [None, 10, 20],   #how deep the trees can go, none means all nodes are expanded
}
gradientboosting_pipeline = Pipeline([
    ('column_select',SelectColumns([])),
    ('gradient_boosting', GradientBoostingRegressor())
])

gradientboosting_grid = {
             'column_select__columns': [

['Gr Liv Area', 'Overall Qual', 'Overall Cond', 'Year Built',
'Kitchen Qual_Ex', 'Kitchen Qual_Gd', 'Kitchen Qual_TA', 'Exter Qual_Ex', 'Exter Qual_Gd', 'Exter Qual_TA', 'Bsmt Qual_Ex', 'Bsmt Qual_Gd', 'Bsmt Qual_TA',
'Fireplaces', 'Lot Area', 'MS SubClass', 'Garage Cars'],
    ],
    'gradient_boosting__n_estimators': [100, 200, 300],  #number of boosting stages, larger number tends to do better
    'gradient_boosting__max_depth': [3, 4, 5],  #limits number of nodes in tree
}
decisiontree_pipeline = Pipeline([
    ('column_select',SelectColumns([])),

    ('decision_tree', DecisionTreeRegressor())
])

decisiontree_grid = {
         'column_select__columns': [


['Gr Liv Area', 'Overall Qual', 'Overall Cond', 'Year Built',
'Kitchen Qual_Ex', 'Kitchen Qual_Gd', 'Kitchen Qual_TA', 'Exter Qual_Ex', 'Exter Qual_Gd', 'Exter Qual_TA', 'Bsmt Qual_Ex', 'Bsmt Qual_Gd', 'Bsmt Qual_TA',
'Fireplaces', 'Lot Area', 'MS SubClass', 'Garage Cars'],
    ],
    'decision_tree__max_depth': [None, 1, 2, 3], #how deep the trees can go, none means all nodes are expanded
    'decision_tree__max_features': [None, "sqrt", "log2"],
}
linearregression_search = GridSearchCV(linearregression_pipe,linearregression_grid,scoring = 'r2', n_jobs = -1)
linearregression_search.fit(xs,ys)

linearregression_r = linearregression_search.best_score_
linearregression_params = linearregression_search.best_params_


randomforest_search = GridSearchCV(randomforest_pipeline, randomforest_grid, scoring='r2', n_jobs=-1)
randomforest_search.fit(xs, ys)

randomforest_params = randomforest_search.best_params_
randomforest_r = randomforest_search.best_score_


decisiontree_search = GridSearchCV(decisiontree_pipeline, decisiontree_grid, scoring='r2', n_jobs=-1)
decisiontree_search.fit(xs, ys)

decisiontree_params = decisiontree_search.best_params_
decisiontree_r = decisiontree_search.best_score_


gradientboosting_search = GridSearchCV(gradientboosting_pipeline, gradientboosting_grid, scoring='r2', n_jobs=-1)
gradientboosting_search.fit(xs, ys)

gradientboosting_params = gradientboosting_search.best_params_
gradientboosting_r = gradientboosting_search.best_score_

print("Random Forest:")
print(f"R-squared: {randomforest_r}")
print(f"Best params: {randomforest_params}\n")

print("Linear Regression:")
print(f"R-Squard: {linearregression_r}")
print(f"Best params: {linearregression_params}\n")

print("Gradient Boosting:")
print(f"R-squared: {gradientboosting_r}")
print(f"Best params: {gradientboosting_params}\n")

print("Decision Tree:")
print(f"R-squared: {decisiontree_r}")
print(f"Best params: {decisiontree_params}\n")