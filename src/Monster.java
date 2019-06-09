class Monster extends Entity implements MonsterAI
{
    private Tile spawnLocation;

    public void setSpawnLocation(Tile spawnlocus){spawnLocation = spawnlocus; }
    public Tile spawnLocation() {return spawnLocation; }

    public void chasePlayer() {

    }



}