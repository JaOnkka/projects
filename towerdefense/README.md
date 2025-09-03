Tower Defense Game: Jake Onkka

The gameplay switches between a build mode and wave mode.

In Build mode, the tilemap is turned on by default so the player can see the tiles available to place defenses.
The player starts with 100 currency and must have enough money in order to place specific defenses, seen on the
right side bar. You cannot place defenses on top of each other, you'd have to click to remove the defense first then
you can replace it with a new defense. You are limited to 15 towers that can help you defend by attacking the nearest enemy.

By pressing Start while in Build mode, you are moved into wave mode. Enemies are spawned and pathfind to the home
which is tile 10x10, the center of the screen.
The player is seen as an archer that can move around using WASD keys and point and left click in order to shoot
at enemies. The player cannot pass through defenses except for the base so if you want to move around, be sure to
leave at least a 1 tile wide gap in your defenses to walk through, keep in mind enemies may target this vulnerability.

Once every enemy is defeated, the wave is won and switches back to build mode. OR if the base is destroyed, everything
is reset and the player resumes in build mode on wave 0.

If the wave was won, then every defense gets repaired automatically so the player doesn't need to worry about
repairing structures, only replacing them if they get destroyed in the wave.

The player additionally has the option of choosing Endless which is a very simple endless gamemode that
"randomly" places enemies on the map continuously to attack the base. In this mode the player cannot go back
into build mode and will fight until the end. You are scored by a survival timer seen in the bottom right while
in this mode.


Commands:
dijkstra - toggles the dijkstra values for each tile as well as the health of every tile

tiles - toggles the tilemap so you can view the tilemap while still in wave mode

money - gives you plenty of money to build whatever defenses you want

base <int> - change the base health to <int>, note you can end game by changing this <= 0