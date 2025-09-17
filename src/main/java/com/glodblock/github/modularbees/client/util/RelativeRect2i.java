package com.glodblock.github.modularbees.client.util;

public class RelativeRect2i implements RelativePosition, Resizable {

    protected int xPos;
    protected int yPos;
    protected int width;
    protected int height;
    protected int offsetX;
    protected int offsetY;

    public RelativeRect2i() {
        this(0, 0, 0, 0);
    }
    
    public RelativeRect2i(int x, int y, int w, int h) {
        this.xPos = x;
        this.yPos = y;
        this.width = w;
        this.height = h;
    }

    @Override
    public int getX() {
        return this.xPos + this.offsetX;
    }

    @Override
    public int getY() {
        return this.yPos + this.offsetY;
    }

    @Override
    public void setOffset(int x, int y) {
        this.offsetX = x;
        this.offsetY = y;
    }

    @Override
    public void setPosition(int x, int y) {
        this.xPos = x;
        this.yPos = y;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public void setSize(int width, int height) {
       this.width = width;
       this.height = height;
    }
    
    public boolean contains(double x, double y) {
        return x >= this.getX() && x <= this.getX() + this.getWidth() && y >= this.getY() && y <= this.getY() + this.getHeight();
    }
    
}
