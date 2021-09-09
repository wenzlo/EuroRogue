package EuroRogue;

public class Light {

    public squidpony.squidmath.Coord position;
    public squidpony.squidgrid.gui.gdx.Radiance radiance;

    public Light(){}

    public Light(squidpony.squidmath.Coord position, squidpony.squidgrid.gui.gdx.Radiance radiance) {

        this.position = position;
        this.radiance = radiance;
    }

    public void setPosition(squidpony.squidmath.Coord newPosition)
    {
        this.position = newPosition;
    }

    public String toString(){

        return position + " " + hashCode() + " " + radiance.toString();
    }
}

