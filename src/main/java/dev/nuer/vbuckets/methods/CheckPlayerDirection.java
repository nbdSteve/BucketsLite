package dev.nuer.vbuckets.methods;

import dev.nuer.vbuckets.file.LoadProvidedFiles;

/**
 * Class for checking a players direction, this is used for horizontal gen buckets.
 * There are a lot of instance variables in this class, but they need to be stored.
 */
public class CheckPlayerDirection {
    //Store the starting point for generation
    private int startPoint;
    //Store the starting point for generation
    private int endPoint;
    //If direction affects the x coordinate this will being used
    private int blockX;
    //If direction affects the z coordinate this will being used
    private int blockZ;
    //Store if the bucket is generating in the positive x direction
    private boolean positiveX;
    //Store if the bucket is generating in the negative x direction
    private boolean negativeX;
    //Store if the bucket is generating in the positive z direction
    private boolean positiveZ;
    //Store if the bucket is generating in the negative z direction
    private boolean negativeZ;

    /**
     * Constructor to check the direction that the player is facing, use this direction for the
     * gen bucket
     *
     * @param facing     the players current yaw
     * @param lpf        LoadPluginFiles instance
     * @param bucketType the string of the bucket number
     * @param x          int, starting block x coordinate
     * @param z          int, starting block z coordinate
     */
    public CheckPlayerDirection(double facing, LoadProvidedFiles lpf, String bucketType, int x,
                                int z) {
        //Store the current values or x, z - if they aren't modified they still need to be
        // returned for the interact event class
        blockX = x;
        blockZ = z;
        if (facing <= 45 || facing > 315) {
            endPoint = z + ((lpf.getBuckets().getInt(bucketType + ".horizontal-gen" +
                    "-length") - 1));
            blockZ = z + 1;
            startPoint = z;
            positiveZ = true;
        } else if (facing > 45 && facing <= 135) {
            endPoint = x - ((lpf.getBuckets().getInt(bucketType + ".horizontal-gen" +
                    "-length") - 1));
            blockX = x - 1;
            startPoint = x;
            negativeX = true;
        } else if (facing > 135 && facing <= 225) {
            endPoint = z - ((lpf.getBuckets().getInt(bucketType + ".horizontal-gen" +
                    "-length") - 1));
            blockZ = z - 1;
            startPoint = z;
            negativeZ = true;
        } else if (facing > 225 && facing <= 315) {
            endPoint = x + ((lpf.getBuckets().getInt(bucketType + ".horizontal-gen" +
                    "-length") - 1));
            blockX = x + 1;
            startPoint = x;
            positiveX = true;
        }
    }

    /**
     * Getter for positiveX boolean
     *
     * @return boolean
     */
    public boolean positiveX() {
        return positiveX;
    }

    /**
     * Getter for negativeX boolean
     *
     * @return boolean
     */
    public boolean negativeX() {
        return negativeX;
    }

    /**
     * Getter for positiveZ boolean
     *
     * @return boolean
     */
    public boolean positiveZ() {
        return positiveZ;
    }

    /**
     * Getter for negativeZ boolean
     *
     * @return boolean
     */
    public boolean negativeZ() {
        return negativeZ;
    }

    /**
     * Getter for the block x coordinate
     *
     * @return int, block x coordinate
     */
    public int getBlockX() {
        return blockX;
    }

    /**
     * Getter for the block x coordinate
     *
     * @return int, block x coordinate
     */
    public int getBlockZ() {
        return blockZ;
    }

    /**
     * Getter for the generation starting block
     *
     * @return int, starting block coordinate
     */
    public int getStartPoint() {
        return startPoint;
    }

    /**
     * Getter for the generation end block
     *
     * @return int, end block coordinate
     */
    public int getEndPoint() {
        return endPoint;
    }
}
