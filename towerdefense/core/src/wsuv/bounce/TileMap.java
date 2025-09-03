package wsuv.bounce;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;




public class TileMap extends Sprite{
    public int healthbase = 10, healthwood = 3, healthstone = 5, healthgold = 7, healthobsidian = 10, healthtower = 6;
    public int tilebase = 2, tilewood = 3, tilestone = 4, tilegold = 5, tileobsidian = 6, tiletower = 20;
    private static final int TILE_SIZE = 40;
    private int[][] tiles;
    private int[][] health;

    private int mapWidth;
    private int mapHeight;
    private int[][] distance; //store distance values for pathfinding
    private boolean[][] visited; //track visited tiles

    private Texture texture;
    public TileMap(BounceGame game, int width, int height) {
       // super(game.am.get("emptytile.png", Texture.class));

        mapWidth = width;
        mapHeight = height;
        tiles = new int[width][height];
        distance = new int[width][height];
        health = new int[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = 0;
                health[x][y] = 0;
            }
        }
    }
    public void resetHP() {
        for (int x = 0; x < 21; x++) {
            for (int y = 0; y < 20; y++) {
                int tileType = tiles[x][y];
                int tilehealth = 0;
                if (tileType == 0) {
                    tilehealth = 0; //0 for empty land
                } else if (tileType == tilewood) {
                    tilehealth = healthwood; //value for walls
                } else if (tileType == tilestone) {
                    tilehealth = healthstone;
                } else if (tileType == tilegold) {
                    tilehealth = healthgold;
                } else if (tileType == tileobsidian) {
                    tilehealth = healthobsidian;
                } else if (tileType == tiletower) {
                    tilehealth = healthtower;
                } else if (tileType == tilebase){
                    tilehealth = healthbase;
                }
                health[x][y] = tilehealth;
            }
        }
    }

    public void setTile(int x, int y, int tileType, int tileHealth) {
        if (x >= 0 && x < mapWidth && y >= 0 && y < mapHeight) {
            tiles[x][y] = tileType;
            health[x][y] = tileHealth;

        }
    }

    public int getTile(int x, int y) {
        if (x >= 0 && x < mapWidth && y >= 0 && y < mapHeight) {
            return tiles[x][y];
        }
        return -1;
    }
    public int getTileHealth(int x, int y) {
        if (x >= 0 && x < mapWidth && y >= 0 && y < mapHeight) {
            return health[x][y];
        }
        return 0;
    }
    public int getTileValue(int[][] dijkstraValues, int x, int y){
        return dijkstraValues[x][y];
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public int getTileSize() {
        return TILE_SIZE;
    }

    private void initializeVisitedArray() {
        visited = new boolean[mapWidth][mapHeight];
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                visited[x][y] = false;
            }
        }
    }
    public void renderDijkstraValues(int[][] dijkstraValues, BounceGame game, BitmapFont font) {
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                int value = dijkstraValues[x][y];
                int health = getTileHealth(x,y);
                font.draw(game.batch, String.valueOf(value), (x * 40) + 20, (y * 40) + 15);
                font.draw(game.batch, String.valueOf(health), (x * 40) + 20, (y * 40) + 35);
            }
        }
    }
    public int[][] dijkstra(int baseX, int baseY) {
        int[][] distances = new int[mapWidth][mapHeight];
        initializeVisitedArray();

        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                distances[x][y] = Integer.MAX_VALUE;
            }
        }

        int source = baseX + baseY * mapWidth;
        distances[baseX][baseY] = 0;

        for (int i = 0; i < mapWidth * mapHeight; i++) {
            int minDistance = Integer.MAX_VALUE;
            int currentX = -1;
            int currentY = -1;

            for (int x = 0; x < mapWidth; x++) {
                for (int y = 0; y < mapHeight; y++) {
                    if (!visited[x][y] && distances[x][y] < minDistance) {
                        minDistance = distances[x][y];
                        currentX = x;
                        currentY = y;
                    }
                }
            }

            if (currentX == -1 || currentY == -1) {
                break; //nowhere else to go
            }

            visited[currentX][currentY] = true;

            //define neighbors (up, down, left, right)
            int[] dx = {0, 0, -1, 1};
            int[] dy = {1, -1, 0, 0};

            for (int j = 0; j < 4; j++) {
                int nx = currentX + dx[j];
                int ny = currentY + dy[j];

                if (nx >= 0 && nx < mapWidth && ny >= 0 && ny < mapHeight) {
                    int movementCost = getMovementCost(nx, ny);
                    if (movementCost != Integer.MAX_VALUE && movementCost != 0 &&
                            distances[currentX][currentY] + movementCost < distances[nx][ny]) {
                        distances[nx][ny] = distances[currentX][currentY] + movementCost;
                    }
                }
            }
        }

        return distances;
    }


    public int getMovementCost(int x, int y) {
        int tileType = tiles[x][y];
        if (tileType != 0){
            return health[x][y] + 1;
        }
        else{ return 1;}/*
        if (tileType == 0) {
            return 1; //1 for empty land
        } else if (tileType == 1) {
            return 10; //value for walls
        } else if (tileType == 3){
            return 3;
        } else if(tileType == 4){
            return 5;
        } else if(tileType == 5){
            return 7;
        } else if(tileType == 6){
            return 10;
        }else {
            return Integer.MAX_VALUE;
        }*/
    }

    public int getShortestPathDistance(int x, int y) {
        return distance[x][y];
    }
}

