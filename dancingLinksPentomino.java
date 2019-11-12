import java.util.ArrayList;
import java.util.List;

/**
 * Richtige Ergebnisse:
 * a(0) = 0;
 * a(1) = 0;
 * a(3) = 160;
 * <p>
 * Die von unserem Code:
 * a(0) = 1;
 * a(1) = 0;
 * a(2) = 33;
 * a(3) = 160;
 * a(4) = 7483;
 * a(5) = 99742;
 * a(6) = 2367340;
 * <p>
 *
 */
// Used Stones
//Pentomino: L U X  & Domino

public class dancingLinksPentomino {

    public static int cnt = 0;
    public static DLXNode h = new DLXNode(); // entry node
    public static DLXNode[] headers;//Matrix um die Nodes zu verknüpfen
    public static boolean printMatrix = false;
    public static final int SIZE = 7;

    /**
     * search tries to find and count all complete coverings of the DLX matrix.
     * Is a recursive, depth-first, backtracking algorithm that finds
     * all solutions to the exact cover problem encoded in the DLX matrix.
     * each time all columns are covered, static long cnt is increased
     *
     * @param k: number of level
     */
    public static void search(int k) { // finds & counts solutions
        if (h.R == h) {
            cnt++;
            return;
        }     // if empty: count & done
        DLXNode c = h.R;                   // choose next column c
        cover(c);                          // remove c from columns
        for (DLXNode r = c.D; r != c; r = r.D) {  // forall rows with 1 in c
            for (DLXNode j = r.R; j != r; j = j.R) // forall 1-elements in row
                cover(j.C);                    // remove column
            search(k + 1);                    // recursion
            for (DLXNode j = r.L; j != r; j = j.L) // forall 1-elements in row
                uncover(j.C);                  // backtrack: un-remove
        }
        uncover(c);                        // un-remove c to columns
    }

    /**
     * cover "covers" a column c of the DLX matrix
     * column c will no longer be found in the column list
     * rows i with 1 element in column c will no longer be found
     * in other column lists than c
     * so column c and rows i are invisible after execution of cover
     *
     * @param c: header element of column that has to be covered
     */
    public static void cover(DLXNode c) { // remove column c
        c.R.L = c.L;                         // remove header
        c.L.R = c.R;                         // .. from row list
        for (DLXNode i = c.D; i != c; i = i.D)      // forall rows with 1
            for (DLXNode j = i.R; i != j; j = j.R) {   // forall elem in row
                j.D.U = j.U;                     // remove row element
                j.U.D = j.D;                     // .. from column list
            }
    }

    /**
     * uncover "uncovers" a column c of the DLX matrix
     * all operations of cover are undone
     * so column c and rows i are visible again after execution of uncover
     *
     * @param c: header element of column that has to be uncovered
     */
    public static void uncover(DLXNode c) {//undo remove col c
        for (DLXNode i = c.U; i != c; i = i.U)      // forall rows with 1
            for (DLXNode j = i.L; i != j; j = j.L) {   // forall elem in row
                j.D.U = j;                       // un-remove row elem
                j.U.D = j;                       // .. to column list
            }
        c.R.L = c;                           // un-remove header
        c.L.R = c;                           // .. to row list
    }


//-----------------------------------------------------------------------------------


    private static void createHead(int n) {
        headers = new DLXNode[n * SIZE];
        //create header nodes
        headers[0] = new DLXNode();
        DLXNode temp = headers[0];
        for (int index = 1; index < headers.length; index++) {
            // create new node
            headers[index] = new DLXNode();
            temp.R = headers[index];
            headers[index].L = temp;
            // set new temp item
            temp = headers[index];
        }
        //connect starting node
        h.R = headers[0];
        headers[0].L = h;
        h.L = headers[(n * SIZE) - 1];
        headers[(n * SIZE) - 1].R = h;
    }

    /**
     * merge given list into the headers matrix
     *
     * @param list
     */
    private static void mergeListintoDL(List<int[]> list) {

        DLXNode[] newRow;
        for (int index = 0; index < list.size(); index++) {
            int[] elem = list.get(index);
            // create row
            newRow = new DLXNode[elem.length];
            for (int arrayIndex = 0; arrayIndex < elem.length; arrayIndex++) {
                if (elem[arrayIndex] == 1) {
                    // create node
                    newRow[arrayIndex] = new DLXNode();
                }

            }
            //link nodes---------------------
            //left side
            DLXNode leftSide = null;
            for (int arrayIndex = 0; arrayIndex < newRow.length; arrayIndex++) {
                // search from the left side to find a valid node
                if (newRow[arrayIndex] != null) {
                    leftSide = newRow[arrayIndex];
                    break;
                }
            }
            //right side
            DLXNode rightSide = null;
            for (int arrayIndex = newRow.length - 1; arrayIndex > -1; arrayIndex--) {
                // search from the right side to find a valid node
                if (newRow[arrayIndex] != null) {
                    rightSide = newRow[arrayIndex];
                    break;
                }
            }

            leftSide.L = rightSide;
            rightSide.R = leftSide;

            DLXNode holder = null;
            for (int arrayIndex = 0; arrayIndex < newRow.length; arrayIndex++) {
                // search the next item
                if (newRow[arrayIndex] != null) {
                    // set first item from row
                    if (holder == null) {
                        holder = newRow[arrayIndex];
                        holder.C = headers[arrayIndex];
                        DLXNode lastInColum = lastNodeInColum(arrayIndex);
                        holder.U = lastInColum;
                        lastInColum.D = holder;
                        continue;
                    }
                    // found next now link
                    holder.R = newRow[arrayIndex];
                    newRow[arrayIndex].L = holder;
                    newRow[arrayIndex].C = headers[arrayIndex];
                    DLXNode lastInColum = lastNodeInColum(arrayIndex);
                    newRow[arrayIndex].U = lastInColum;
                    lastInColum.D = newRow[arrayIndex];
                    // set as last link
                    holder = newRow[arrayIndex];
                }
            }

            // debug outprint
            if (printMatrix) {
                for (int i = 0; i < newRow.length; i++) {
                    if (newRow[i] == null) {
                        System.out.print("0 ");
                    } else {
                        System.out.print("1 ");
                    }

                }
                System.out.println("");
            }
        }
    }

    /**
     * debug one colum of matrix
     *
     * @param index of the colum
     */
    private static void printColumOfMatrix(int index) {

        System.out.println("------------");
        DLXNode x = headers[index];
        DLXNode y = null;
        boolean found = false;
        while (!found) {
            if (y == null) y = x.D;
            DLXNode c = y.D;
            if (c.equals(x)) {
                // found last
                System.out.print("1 ");
                found = true;

            } else {
                //go deeper
                System.out.print("1 ");
                y = c;
            }
        }
        System.out.println("");

    }

    /**
     * return last node in colum
     *
     * @param index
     * @return
     */
    private static DLXNode lastNodeInColum(int index) {
        DLXNode x = headers[index];
        DLXNode y = null;
        boolean found = false;
        while (!found) {
            if (y == null) y = x.D;
            DLXNode c = y.D;
            // item points to it self
            if (c.equals(y)) {
                // found last
                found = true;
                return y;
            } else {
                //go deeper
                y = c;
            }
        }
        return y;
    }

    /**
     * connect last nodes to the header element
     */
    private static void connectLastNodesWithHeader() {
        for (int index = 0; index < headers.length; index++) {
            DLXNode last = lastNodeInColum(index);
            headers[index].U = last;
            last.D = headers[index];
        }
    }

    /**
     * create all Combinations of all figures and merge it into the DL List
     *
     * @param n input size
     */
    private static void createCombinations(int n) {

        List<int[]> holder = new ArrayList<>();

        // Create Domino
        holder.addAll(createForm(new int[] { 1,1 },n, 1,2));
        holder.addAll(createForm(new int[] { 1,1 },n, 2,1));




        // Stehend L
        List<int[]> formSL = new ArrayList<>();
        formSL.add(new int[] { 1,1,1,0,1,0,1,0 });
        formSL.add(new int[] { 1,1,0,1,0,1,0,1 });
        formSL.add(new int[] { 1,0,1,0,1,0,1,1 });
        formSL.add(new int[] { 0,1,0,1,0,1,1,1 });

       for (int index = 0; index < formSL.size(); index++){
           holder.addAll(createForm(formSL.get(index),n, 4,2));
       }
       formSL= null;

       // liegend L
        List<int[]> formlL = new ArrayList<>();
        formlL.add(new int[] { 1,1,1,1,1,0,0,0 });
        formlL.add(new int[] { 1,1,1,1,0,0,0,1 });
        formlL.add(new int[] { 1,0,0,0,1,1,1,1 });
        formlL.add(new int[] { 0,0,0,1,1,1,1,1 });
        for (int index = 0; index < formlL.size(); index++){
            holder.addAll(createForm(formlL.get(index),n, 2,4));
        }

        formlL = null;

        // create U
        List<int[]> formSU = new ArrayList<>();
        formSU.add(new int[] { 1,1,1,1,0,1 });
        formSU.add(new int[] { 1,0,1,1,1,1 });
        for (int index = 0; index < formSU.size(); index++){
            holder.addAll(createForm(formSU.get(index),n, 2,3));
        }
        formSU = null;
        List<int[]> formlU = new ArrayList<>();
        formlU.add(new int[] { 1,1,1,0,1,1 });
        formlU.add(new int[] { 1,1,0,1,1,1 });
        for (int index = 0; index < formlU.size(); index++){
            holder.addAll(createForm(formlU.get(index),n, 3,2));
        }
        formlU = null;

        // create x
        holder.addAll(createForm(new int[] { 0,1,0,1,1,1,0,1,0 },n, 3,3));

        System.gc();
        //create matix
        createHead(n);

        mergeListintoDL(holder);
        // connect last nodes to the head
        connectLastNodesWithHeader();
    }

// TODO
    private static List<int[]> createForm(int[] form, int size, int height, int width) {
        List<int[]> combis = new ArrayList<>();
        final int FLAECHE = size* SIZE;

        if(width > size){
            return combis;
        }

        int columCounter = width;
        int currentRow = 0;
        for (int shift = 0; shift < FLAECHE; shift++) {
            // CHECK IF over top corner
            if (currentRow + height > 7) {
                break;
            }
            // CHECK IF over corner site
            if (columCounter > size) {
                // jump into next row
                columCounter = width;
                currentRow++;
                shift = currentRow*size -1; // -1 for the for ++ and the continue

                continue;
            }

            int[] row = new int[(FLAECHE)];

            // pase into the row
            int rowIndex = shift;
            int rowHeight = 1;
            for(int index = 1; index-1 < form.length; index++){

                row[rowIndex] = form[ index-1 ];
                rowIndex++;
                if(index % width == 0){
                    rowIndex = shift + rowHeight * size;
                    rowHeight++;
                }
            }

            // print out
            if(printMatrix){
                for(int index = 0 ; index < row.length; index++){
                    if(index % size == 0){
                        System.out.println("");
                    }
                    System.out.print(row[index]);
                }
                System.out.println("");
            }

            combis.add(row);
            columCounter++;

        }
        return combis;
    }

    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);

        if (n == 1) {
            System.out.println("Die Funktion findet: 0 Kombination");
            return;
        }
        //create Matrix
        createCombinations(n);
        search(0);
        System.out.println("Die Funktion findet: " + cnt + " Kombinationen");
    }
}

/**
 * Class DLXNode
 * represents a matrix element of the cover matrix with value 1
 * links go to up down left right neigbors, and column header
 * can also be used as colm header or root of column headers
 * matrix is sparsely coded
 * try to do all operations very efficiently
 * see:
 * http://en.wikipedia.org/wiki/Dancing_Links
 * http://arxiv.org/abs/cs/0011047
 */
class DLXNode {       // represents 1 element or header
    DLXNode C;           // reference to column-header
    DLXNode L, R, U, D;  // left, right, up, down references

    DLXNode() {
        C = L = R = U = D = this;
    } // supports circular lists
}
