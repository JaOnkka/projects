## CS483 Project 4
## Jake Onkka
## Clustering - using KMeans to cluster types of Pokemon based on their stats
import pandas as pd
from sklearn.cluster import KMeans
from sklearn.preprocessing import MinMaxScaler
from sklearn.pipeline import Pipeline
from sklearn.metrics import silhouette_score
import warnings     #there are some memory leak warnings from kmeans on windows
warnings.filterwarnings('ignore')
pokemon_df = pd.read_csv("Pokemon.csv")

pokemon_types = pokemon_df["Type 1"].unique()

cluster_info_dfs = []


def cluster_pokemon_by_type(pokemon_type, final):

    type_df = pokemon_df[pokemon_df["Type 1"] == pokemon_type]  # .reset_index(drop=True)

    stats_columns = ["HP", "Attack", "Defense", "Sp. Atk", "Sp. Def", "Speed"]
    stats_data = type_df[stats_columns]

    pipeline = Pipeline([
        ('scaler', MinMaxScaler()),
        ('cluster', KMeans())
    ])

    silhouette_scores = []
    best_score = -1
    best_clusters = 2
    num_labels = len(type_df)
    # print(num_labels)
    for n_clusters in range(2, min(num_labels, 15)):
        pipeline.set_params(cluster__n_clusters=n_clusters)
        pipeline.fit(stats_data)
        labels = pipeline.predict(stats_data)

        score = silhouette_score(stats_data, labels - 1)
        silhouette_scores.append((n_clusters, score))

        if score > best_score:
            best_score = score
            best_clusters = n_clusters

    cluster_info_df = pd.DataFrame(silhouette_scores, columns=["Clusters", "Silhouette Coefficient"])
    cluster_info_df["Type"] = pokemon_type
    cluster_info_dfs.append(cluster_info_df)

    if (final == 0):
        #print silhouette coefficient scores for each cluster number
        print(f"{pokemon_type}\n{'-----------'}")
        for n_clusters, score in silhouette_scores:
            print(f"{n_clusters} clusters: {score}")

        print(f"best number of clusters: {best_clusters}")
        print(f"best score: {best_score}\n")

    pipeline.set_params(cluster__n_clusters=best_clusters)
    pipeline.fit(stats_data)
    labels = pipeline.predict(stats_data)
    type_df["Cluster"] = labels
    if (final == 1):
        print(f"{pokemon_type}\n{'----'}")

        for cluster_num in range(best_clusters):
            cluster_data = type_df[type_df["Cluster"] == cluster_num][["Name"] + stats_columns]

            print(f"Cluster {cluster_num}")
            print(cluster_data)


            print("Mean HP: " + str(cluster_data["HP"].mean()))
            print("Mean Attack: " + str(cluster_data["Attack"].mean()))
            print("Mean Defense: " + str(cluster_data["Defense"].mean()))
            print("Mean Sp. Atk: " + str(cluster_data["Sp. Atk"].mean()))
            print("Mean Sp. Def: " + str(cluster_data["Sp. Def"].mean()))
            print("Mean Speed: " + str(cluster_data["Speed"].mean()))
            print("\n")

for pokemon_type in pokemon_types:
    cluster_pokemon_by_type(pokemon_type, 0)
for pokemon_type in pokemon_types:
    cluster_pokemon_by_type(pokemon_type, 1)
