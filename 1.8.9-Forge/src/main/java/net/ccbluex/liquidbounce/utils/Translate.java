package net.ccbluex.liquidbounce.utils;

public final class Translate {
    private float x;
    private float y;

    public Translate(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public final void interpolate(float targetX, float targetY, double smoothing) {
        this.x = (float) AnimationUtils.animate(targetX, this.x, smoothing);
        this.y = (float) AnimationUtils.animate(targetY, this.y, smoothing);
    }

    public float getX() {
        return this.x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return this.y;
    }

    public void setY(float y) {
        this.y = y;
    }
}