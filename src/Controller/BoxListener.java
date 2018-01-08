package controller;

import view.WarehouseView;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


/**
 * Material pràctica 1 Programació Avançada i Estructura de Dades
 * Enginyeria Informàtica
 * © La Salle Campus Barcelona - Departament d'Enginyeria
 *
 * Aquesta classe permet l'actualització de la GUI amb les dades del
 * magatzem donat que escolta els clics realitzats sobre la interfície
 * gràfica. El mètode 'mouseClicked' es crida cada cop que una casella
 * del magatzem que sigui d'una prestatgeria s'hagi clicat.
 *
 * Aquesta classe és modificable, en cap cas ha de suposar una limitació
 * en com ha estat implementada. És a dir, si necessiteu algun paràmetre
 * extra o atribut (o bé us sobren) teniu tot el dret a canviar-ho. L'únic
 * que s'ha de mantenir i completar és el mètode 'mouseClicked'.
 *
 * @author Albert Pernía Vázquez
 */
public class BoxListener implements MouseListener {


    private WarehouseView view;
    // ...



    public BoxListener(WarehouseView view) {
        this.view = view;
        // ...
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        Point point = e.getPoint();

        Point p = view.getBoxClickedPosition(point);
        if (p == null) System.out.println("null point");
        else {
            // TODO: deixar a l'alumne per fer
            // ...
            System.out.println("han fet clic...");
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
