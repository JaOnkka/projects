## CS483 Project 2
## Jake Onkka
## Pipelines - House pricing regression

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

data = pd.read_csv('AmesHousing.csv')
data['Total Bsmt SF'] = data['Total Bsmt SF'].fillna(0)
data['Garage Cars'] = data['Garage Cars'].fillna(0)
data['Fireplaces'] = data['Fireplaces'].fillna(0)
data['BsmtFin SF1'] = data['BsmtFin SF 1'].fillna(0)
data['BsmtFin SF 2'] = data['BsmtFin SF 2'].fillna(0)
data['TotRms AbvGrd'] = data['TotRms AbvGrd'].fillna(0)
data['Central Air'] = data['Central Air'].fillna(0)


data = pd.get_dummies(data)

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

steps = [
    ('column_select',SelectColumns([])),
    ('linear_regression', regressor),
]
pipe = Pipeline(steps)
xs = data.drop(columns = ['SalePrice'])
ys = data['SalePrice']


grid = { 'column_select__columns': [
    ['Gr Liv Area', 'Overall Qual', 'Overall Cond', 'Year Built',
    'Neighborhood_Gilbert', 'Neighborhood_StoneBr', 'Neighborhood_NAmes', 'Neighborhood_NWAmes', 'Neighborhood_Somerst', 'Neighborhood_BrDale', 'Neighborhood_NridgHt', 'Neighborhood_Blmngtn', 'Neighborhood_SawyerW', 'Neighborhood_Sawyer', 'Neighborhood_BrkSide', 'Neighborhood_OldTown', 'Neighborhood_ClearCr', 'Neighborhood_SWISU', 'Neighborhood_Edwards', 'Neighborhood_CollgCr', 'Neighborhood_Blueste', 'Neighborhood_NoRidge', 'Neighborhood_Mitchel', 'Neighborhood_IDOTRR', 'Neighborhood_Crawfor', 'Neighborhood_Timber', 'Neighborhood_MeadowV',
    'Kitchen Qual_Ex', 'Kitchen Qual_Gd', 'Kitchen Qual_TA', 'Exter Qual_Ex', 'Exter Qual_Gd', 'Exter Qual_TA', 'Bsmt Qual_Ex', 'Bsmt Qual_Gd', 'Bsmt Qual_TA',
    'Fireplaces', 'Lot Area', 'MS SubClass', 'Garage Cars'],
    ],
    'linear_regression': [
        LinearRegression( n_jobs = -1 ),
        TransformedTargetRegressor(
            LinearRegression( n_jobs = -1 ),
            func = np.sqrt,
            inverse_func = np.square ),
        TransformedTargetRegressor(
            LinearRegression( n_jobs = -1 ),
            func = np.cbrt,
            inverse_func = lambda y: np.power( y, 3 ) ),
        TransformedTargetRegressor(
            LinearRegression( n_jobs = -1 ),
            func = np.log,
            inverse_func = np.exp),
    ]
}

search = GridSearchCV(pipe,grid,scoring = 'r2', n_jobs = -1)

search.fit(xs,ys)
r = search.best_score_
params = search.best_params_
print(r)
print(params)