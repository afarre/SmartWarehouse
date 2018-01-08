package view;

import controller.BoxListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.util.ArrayList;


/**
 * Permet la visualització dels productes en el magatzem així
 * com l'actualització de la gràfica amb les dades dels productes
 * o el canvi de color de les caselles.
 *
 * @author Albert Pernía Vázquez
 */
public class WarehouseView extends JFrame {

    private static final int MIN_WIDTH  = 450;
    private static final int MIN_HEIGHT = 300;

    private static final String MIN_SCORE_LABEL_FORMAT      = "Min score: %f";
    private static final String TRACK_COST_LABEL_FORMAT     = "Track cost: %d";


    private JLabel[] jlInfo;
    private JLabel   jlMinScore;
    private JLabel   jlTrackScore;
    private JPanel   jpGrid;

    private ArrayList<GridComponent> boxInGrid;

    private int mapDimension;

    /**
     *
     * @param map       Matriu de booleans on en cas que la posició sigui 'certa' indica que existeix
     *                  una caixa d'una prestatgeria en aquella posició. Si, en canvi, el valor és 'fals'
     *                  llavors és un espai lliure.
     * @param entranceX Punt d'entrada al magatzem respecte l'eix OX.
     * @param entranceY Punt d'entrada al magatzem respecte l'eix OY.
     */
    public WarehouseView(boolean [][]map, int entranceX, int entranceY) {

        assert map.length > 0;

        mapDimension = map.length;

        this.getContentPane().setLayout(new BorderLayout());

        for (int i = 0; i < this.getContentPane().getMouseListeners().length; i++)
            this.getContentPane().removeMouseListener(this.getContentPane().getMouseListeners()[i]);

        this.jpGrid = createWarehouseMap(map.length, map[0].length, map, entranceX, entranceY);

        this.getContentPane().add(
                jpGrid,
                BorderLayout.CENTER
        );
        this.getContentPane().add(
                createInfoPanel(),
                BorderLayout.SOUTH
        );

        this.getContentPane().add(new JLabel("Y AXIS"), BorderLayout.WEST);
        JPanel jpUp = new JPanel();
        jpUp.setLayout(new FlowLayout());
        jpUp.add(new JLabel("X AXIS"));
        this.getContentPane().add(jpUp, BorderLayout.NORTH);

        this.setTitle(ViewConstants.TITLE);
        this.setVisible(true);
        this.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }


    private JPanel createWarehouseMap(int x, int y, boolean [][]map, int entranceX, int entranceY) {
        JPanel jpAll = new JPanel();

        jpAll.setLayout(new GridLayout(y, x, 1, 1));

        boxInGrid = new ArrayList<>();

        for (int i = 0; i < y; i++) {
            for (int j = 0; j < x; j++) {

                if (i == entranceY && j == entranceX) {

                    JPanel jp = new JPanel();
                    jp.setBackground(ViewConstants.START_COLOR);
                    jpAll.add(jp);
                }
                else if (map[j][i]) { // shelve's box

                    GridComponent gc = new GridComponent(j, i);
                    gc.setBackground(ViewConstants.SHELVE_COLOR);
                    boxInGrid.add(gc);
                    jpAll.add(gc);
                }
                else {
                    JPanel jp = new JPanel();
                    jp.setBackground(ViewConstants.WAY_COLOR);
                    jpAll.add(jp);
                }
            }
        }

        return jpAll;
    }

    private JPanel createInfoPanel() {
        JPanel jpAll = new JPanel();

        jpAll.setLayout(new GridLayout(1, 2));

        JPanel jpLeft = new JPanel();
        jpLeft.setLayout(new BorderLayout());
        jlMinScore = new JLabel(String.format(MIN_SCORE_LABEL_FORMAT, 0.0d));
        jpLeft.add(jlMinScore, BorderLayout.CENTER);
        jpLeft.setBorder(BorderFactory.createTitledBorder("Distribution cost"));

        JPanel jpCenter = new JPanel();
        jpCenter.setLayout(new BorderLayout());
        jlTrackScore = new JLabel(String.format(TRACK_COST_LABEL_FORMAT, 0));
        jpCenter.add(jlTrackScore, BorderLayout.CENTER);
        jpCenter.setBorder(BorderFactory.createTitledBorder("Track cost"));

        JPanel jpRight = new JPanel();
        jpRight.setLayout(new GridLayout(3, 0));
        jlInfo = new JLabel[3];

        jpRight.setBorder(BorderFactory.createTitledBorder("Selected Box Info"));

        for (int i = 0; i < jlInfo.length; i++) {
            jlInfo[i] = new JLabel();
            jpRight.add(jlInfo[i]);
        }

        jpAll.add(jpLeft);
        jpAll.add(jpCenter);
        jpAll.add(jpRight);

        return jpAll;
    }

    /**
     * Actualitza una casella concreta amb un color específic.
     *
     * @param x     Coordenada de la casella a pintar respecte l'eix OX
     * @param y     Coordenada de la casella a pintar respecte l'eix OY
     * @param color El nou color que obtindrà la casella
     */
    // pre: 0 ≤ x ≤ dimX and 0 ≤ y ≤ dimY
    public void paintCell(int x, int y, Color color) {

        Component component = jpGrid.getComponent(y * mapDimension + x);
        component.setBackground(color);
    }

    /**
     * Actualitza el panell informatiu de la gràfica amb la informació de 3 productes
     * col·locats sobre una mateixa posició (x, y) i altures (z) variants.
     *
     * @param descriptions  La descripció de cadascuna de les 3 caixes en les diferents altures, sent
     *                      l'ordre incremental (d'abaix a dalt).
     */
    public void setBoxInfo(String descriptions[]) {
        int i = 0;

        for (String desc : descriptions)
            jlInfo[i++].setText(desc);
    }

    /**
     * Actualitza el valor de la puntuació mínima del panell de la gràfica.
     *
     * @param score El valor que es mostrarà.
     */
    public void setScoreInfo(double score) {

        this.jlMinScore.setText(String.format(MIN_SCORE_LABEL_FORMAT, score));
    }

    /**
     * Actualitza el valor de cost de la ruta que ha seguit el robot pel magatzem.
     *
     * @param cost  El valor de cost.
     */
    public void setTrackCost(int cost) {

        this.jlTrackScore.setText(String.format(TRACK_COST_LABEL_FORMAT, cost));
    }

    /**
     * Permet la interacció amb la vista.
     *
     * @param listener  La instància d'una classe que implementi MouseListener i que
     *                  controlarà la vista.
     */
    public void setMapMouseListener(MouseListener listener) {

        this.jpGrid.addMouseListener(listener);
    }


    /**
     * Obté la posició (x, y) de la casella que s'ha clicat.
     *
     * @param rawPoint El punt (x,y) però respecte la pantalla i no la matriu.
     *
     * @return El punt (x, y) de la matriu que s'ha clicat.
     */
    public Point getBoxClickedPosition(Point rawPoint) {

        Component component = jpGrid.getComponentAt(rawPoint);

        if (!(component instanceof GridComponent)) return null;

        for (GridComponent gc : boxInGrid) {

            if (component == gc) {

                return new Point(gc.x, gc.y);
            }
        }

        return null;
    }



    private class GridComponent extends JPanel {

        int x;
        int y;

        GridComponent(int x, int y) {

            super();
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Un exemple de com utilitzar la gràfica
     * @param args
     */
    public static void main(String []args) {


        boolean [][]matrix = new boolean[7][4]; // instanciem una matriu que representa el magatzem

        for (int i = 0; i < matrix[0].length - 1; i++)
            matrix[0][i] = matrix[2][i] = true; // afegim prestatgeries

        // Creem la vista
        // usem el magatzem amb la matriu anterior i el punt (x,y) d'entrada
        WarehouseView view = new WarehouseView(matrix, 1, 0);

        // Creem el controlador
        BoxListener controlador = new BoxListener(view);
        // Establim la relació entre la vista i el controlador
        view.setMapMouseListener(controlador);

        /* Actualitzem el panell d'informació d'una casella en les diferents altures
         * en, imaginariament, el punt (x,y)=(0,0).
        */
        view.setBoxInfo(
                new String[] {
                        "(x,y,z)=(0,0,0) P1 - ID: 1234",
                        "(x,y,z)=(0,0,1) P2 - ID: 4321",
                        "(x,y,z)=(0,0,2) (empty)"
                }
        );

        view.setScoreInfo(5.6); // actualitzem el panell de puntuació de la distribució feta
        view.setTrackCost(15);  // actualitzem el cost que té un recorregut realitzat

        view.paintCell(3, 3, Color.RED);    // la casella en el punt (3,3) passarà a ser vermella
        // Mostrem la vista
        view.setVisible(true);
    }

}