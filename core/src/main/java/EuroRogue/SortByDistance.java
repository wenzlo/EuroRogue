package EuroRogue;

import java.util.Comparator;

import squidpony.squidmath.Coord;

public  class SortByDistance implements Comparator<Coord> {

    Coord origin;

    public SortByDistance(Coord origin) {

        this.origin = origin;

    }

    @Override
    public int compare(Coord coord1, Coord coord2) {

        return Double.compare(origin.distance(coord1), origin.distance(coord2));
    }

}
