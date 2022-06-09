import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class AStarPathingStrategy
        implements PathingStrategy {


    public LinkedList<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors) {


        /*define closed list
          define open list
          while (true){
            Filtered list containing neighbors you can actually move to
            Check if any of the neighbors are beside the target
            set the g, h, f values
            add them to open list if not in open list
            add the selected node to close list
          return path*/

        List<AStarPoint> closedList = new LinkedList<>();
        List<AStarPoint> openList = new LinkedList<>();
        openList.add(new AStarPoint(start, null, end));

        boolean pathComplete = false;

        AStarPoint last = null;

        while(!openList.isEmpty() && !pathComplete) {
            AStarPoint next = findNext(openList);
            openList.remove(next);

            List<Point> potentialNeighborsList = potentialNeighbors.apply(next.point).collect(Collectors.toList());




            List<Point> successors0 = potentialNeighborsList.stream()
                    .filter(canPassThrough)
                    .collect(Collectors.toList());

            List<AStarPoint> successors = successors0.stream().filter(pt ->
                            !pt.equals(start))
                    .map(point -> new AStarPoint(point, next, end))
                    .collect(Collectors.toList());

            for (AStarPoint point :
                    successors) {
                if(point.point.equals(end)) {
                    pathComplete = true;
                    last = next;
                    break;
                } else if(!openList.contains(point) && replaceWorseOptions(point, openList) && replaceWorseOptions(point, closedList)) {
                    openList.add(point);
                }
            }

            if(!closedList.contains(next)){
                closedList.add(next);
            }


        }

        LinkedList<Point> path = new LinkedList<>();

        if(last != null) {
            AStarPoint node = last;

            while(node.parent != null) {
                path.add(node.getPoint());
                node = node.parent;
            }

        }

        return path;
    }






    /**
     * returns true if there are no better options
     * @param a Point to compare against
     * @param b List to look for better options in
     * @return there was no better option
     */
    private boolean replaceWorseOptions(AStarPoint a, List<AStarPoint> b) {
        if(b==null || b.size() == 0) {
            return true;
        }

        List<AStarPoint> bStream = b.stream()
                .filter(p -> p.point.equals(a.point)).collect(Collectors.toList());
        for (AStarPoint point:
             bStream) {
            if(point.getF() > a.getF()) {
                b.remove(point);
                return true;
            } else {
                return false;
            }

        }
        return true;
    }

    private AStarPoint findNext(List<AStarPoint> openList) {
        Comparator<AStarPoint> pointComparator = Comparator.comparing(AStarPoint::getF)
                .thenComparing(AStarPoint::getH);
        Optional<AStarPoint> result = openList.stream().min(pointComparator);

        return result.orElse(null);
    }


    private static class AStarPoint{
        private final Point point;
        private final AStarPoint parent;
        private final Point end;
        private final int g;
        private final int h;
        private final int f;

        /**
         * @param point The location
         * @param parent parent point
         * @param end the end destination
         */
        AStarPoint(Point point, AStarPoint parent, Point end) {
            this.point = point;
            this.parent = parent;
            this.end = end;

            this.g = calculateG();
            this.h = calculateH();
            this.f = g + h;
        }

        int calculateG() {
            if(parent != null){
                return longDistance(point, parent.getPoint()) + parent.getG();
            }
            return 0;
        }

        int calculateH() {
            return longDistance(point, end);
        }

        int getG() {
            return g;
        }

        int getH() {
            return h;
        }

        int getF() {
            return f;
        }

        Point getPoint() {
            return point;
        }

        private int longDistance(Point current, Point end) {

            int distanceX = Math.abs(current.x - end.x);
            int distanceY = Math.abs(current.y - end.y);

            int maxDistance = Math.max(distanceX, distanceY);
            int minDistance = Math.min(distanceX, distanceY);

            return 14 * minDistance + 10*(maxDistance-minDistance);

        }
    }

}
