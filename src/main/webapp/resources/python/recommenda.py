#-*- coding: utf-8 -*- 

from scipy.sparse.linalg import svds

import pandas as pd
import numpy as np
#리뷰
df_ratings = pd.read_csv('./rating_1.csv', encoding = 'euc-kr')
#책
df_books = pd.read_csv('./books_1.csv', encoding = 'euc-kr')

df_user_book_ratings = df_ratings.pivot_table(
    index='userId',
    columns='bookId',
    values='rating'
).fillna(0)

matrix = df_user_book_ratings.values
user_ratings_mean = np.mean(matrix, axis = 1)
matrix_user_mean = matrix - user_ratings_mean.reshape(-1, 1)

U, sigma, Vt = svds(matrix_user_mean, k = 12)
sigma = np.diag(sigma)

svd_user_predicted_ratings = np.dot(np.dot(U, sigma), Vt) + user_ratings_mean.reshape(-1, 1)
df_svd_preds = pd.DataFrame(svd_user_predicted_ratings, columns = df_user_book_ratings.columns)
df_svd_preds.head()

def recommend_books(user_id):
    
    num_recommendations=10
    user_row_number = user_id - 1    
    sorted_user_predictions = df_svd_preds.iloc[user_row_number].sort_values(ascending=False)
    user_data = df_ratings[df_ratings.userId == user_id] 
    user_history = user_data.merge(df_books, on = 'bookId').sort_values(['rating'], ascending=False)
    recommendations = df_books[~df_books['bookId'].isin(user_history['bookId'])]
    recommendations = recommendations.merge( pd.DataFrame(sorted_user_predictions).reset_index(), on = 'bookId')
    recommendations = recommendations.rename(columns = {user_row_number: 'Predictions'}).sort_values('Predictions', ascending = False).iloc[:num_recommendations, :]                 

    result = pd.DataFrame(recommendations["bookId"])
    result = result.values.tolist()

    return result



