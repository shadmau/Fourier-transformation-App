package com.fouriertransformation;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;

import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.CustomLabelFormatter;

import android.widget.LinearLayout;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import android.app.AlertDialog;
import org.apache.commons.math3.analysis.function.Sinc;




public class MyActivity extends Activity{
private GraphView graph,graph2;
private GraphViewSeries rechteckDaten;
private LinearLayout rechteckLayout, transformierteLayout;
private double start,amplitude,breite;


    public void rechteckButton(View view){


        boolean error = false;
        String errorMessage="";

        switch(view.getId()){
            case R.id.amplitudeGroesser:
                amplitude += 0.5;
                break;
            case R.id.amplitudeKleiner:
                if(amplitude<=0.5){error = true; errorMessage="Amplitude darf nicht kleiner als 0.5 sein!";}
                    else{amplitude -=0.5;}
                break;
            case R.id.verschiebungRechts:
                start +=0.5;
                break;
            case R.id.verschiebungLinks:
                if(start<=0.0){error= true; errorMessage="Verschiebung darf nicht negativ sein";}
                    else{start -=0.5;}
                break;
            case R.id.breiteGroesser:
                breite += 0.5;
                break;
            case R.id.breiteKleiner:
                if(breite<=0.5){error=true; errorMessage="Breite darf nicht kleiner als 0.5 sein";}
                    else {breite -= 0.5;}
                break;

        }


        if(error == false){
            graph = updateRechteckGraph(graph,amplitude,start,breite);
            graph2 = updateFourierTransformierte(graph2, amplitude, start, breite);

            updateLabels();
            updateFunktionenTxt(start,amplitude,breite);
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Fehler");
            builder.setMessage(errorMessage).setCancelable(true).setPositiveButton("OK",null);

            AlertDialog dialog = builder.create();
            dialog.show();

        }

    }

    public void updateLabels(){
        TextView amplitudeTxt = (TextView) findViewById(R.id.amplitudeTxt);
        TextView verschiebungTxt = (TextView) findViewById(R.id.verschiebungTxt);
        TextView breiteTxt = (TextView) findViewById(R.id.breiteTxt);

        amplitudeTxt.setText("Amplitude: \t\t" + amplitude);
        verschiebungTxt.setText("Verschiebung: \t" + start);
        breiteTxt.setText("Breite: \t\t\t" + breite);


    }

    public void updateFunktionenTxt(double start, double amplitude, double breite){
        // Rechteckfunktion
        TextView rechteckfunktionTxt = (TextView) findViewById(R.id.rechteckfunktionTxt);
        String htmlOutput = "";
        if(amplitude != 1.0){
            if((amplitude - (int)amplitude) == 0.0){ htmlOutput += (int) amplitude;} else {htmlOutput += amplitude;}
        }
        htmlOutput+= " rect(<sup>" + "t";
        if(start!= 0.0) {
            if ((start - (int) start) == 0.0) {
                htmlOutput += "-" + (int) start;
            } else {
                htmlOutput += "-" + start;
            }
        }
        htmlOutput += "</sup>/";
        if(breite != 1.0) {
            if ((breite - (int) breite) == 0.0 && breite != 0.0) {
                htmlOutput += (int) breite;
            } else {
                htmlOutput += breite;
            }
        }
        htmlOutput += " T)";
        rechteckfunktionTxt.setText( Html.fromHtml(htmlOutput));

        // Fouriertransformierte
        TextView fourierFunktionTxt = (TextView) findViewById(R.id.fourierTransfoFunktionTxt);
        htmlOutput = "";
        if((amplitude-(int)amplitude) == 0.0 ){htmlOutput +=(int) amplitude;} else {htmlOutput += amplitude;}
        htmlOutput +="*";
        if(breite-(int)breite == 0.0){htmlOutput +=(int) breite;} else {htmlOutput +=breite;}
        htmlOutput += " T * si(&#960;*"+breite+"T*f)";
        if(start != 0.0) {
            htmlOutput += "* e^<sup>-j2&#960;f";
            if ((start - (int) start) == 0) {
                htmlOutput += (int) start;

            } else {htmlOutput += start;}
        }
        fourierFunktionTxt.setText(Html.fromHtml(htmlOutput));

    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        start = 0.5;
        breite = 1;
        amplitude = 1;

        graph = new LineGraphView(getApplicationContext(),"Rechtecksignal");
        graph.getGraphViewStyle().setNumHorizontalLabels(3);
        graph.getGraphViewStyle().setNumVerticalLabels(4);
        graph.getGraphViewStyle().setTextSize(25.0f);
        graph.getGraphViewStyle().setGridColor(Color.BLACK);
        graph.getGraphViewStyle().setVerticalLabelsAlign(android.graphics.Paint.Align.CENTER);
        graph.setCustomLabelFormatter(new CustomLabelFormatter() {
            @Override
            public String formatLabel(double v, boolean isValueX) {
                if (isValueX && (int) v != 0) {
                    return (int) v + " T";
                }
                return null;
            }
        });
         transformierteLayout = (LinearLayout) findViewById(R.id.transformierte);
        graph2 = new LineGraphView(this,"Fouriertransformierte");
        graph2.getGraphViewStyle().setTextSize(20);
        graph2.getGraphViewStyle().setNumHorizontalLabels(9);
        graph2.getGraphViewStyle().setNumVerticalLabels(10);
        rechteckLayout = (LinearLayout) findViewById(R.id.rechteckGraph);
        rechteckLayout.addView(updateRechteckGraph(graph, amplitude, start, breite));
        transformierteLayout.addView(updateFourierTransformierte(graph2, amplitude, start, breite));
        updateLabels();
        updateFunktionenTxt(start,amplitude,breite);

    }


    public static GraphView updateFourierTransformierte(GraphView graph, double amplitude, double start, double breite){
        Sinc sinc = new Sinc();

      //  if(start != 0.0) {y = y*Math.pow(Math.E,} to-do
        GraphViewData[] dataRe = new GraphViewData[501];
        GraphViewData[] dataIm = new GraphViewData[501];
        double x= -4;
        double yRe;
        double yIm;
        //Log.v("myapp",((Double)start).toString());
        boolean imgTeil = true;
        if(start == 0.0){imgTeil = false;}
        for(int i=0; i<501; i++){

            yRe = amplitude*breite*sinc.value(breite*x*Math.PI)*Math.cos(-2*Math.PI*start*x);
            dataRe[i] = new GraphViewData(x,yRe);

            if(imgTeil == true){
                yIm = amplitude*breite*sinc.value(breite*x*Math.PI)*Math.sin(-2*Math.PI*start*x);
                dataIm[i] = new GraphViewData(x,yIm);

            }
            x += 0.016;
        }


        GraphViewSeries seriesRe = new GraphViewSeries("Realteil",new GraphViewSeriesStyle(Color.BLUE, 3), dataRe);

        graph.removeAllSeries();
        graph.addSeries(seriesRe);
         if(imgTeil == true) {
             GraphViewSeries seriesIm = new GraphViewSeries("Imaginärteil", new GraphViewSeriesStyle(Color.YELLOW, 3), dataIm);
             graph.addSeries(seriesIm);

         }

        graph.setShowLegend(true);
        graph.setLegendAlign(GraphView.LegendAlign.BOTTOM);
        return graph;

    }
    public static GraphView updateRechteckGraph(GraphView graph, double amplitude, double start, double breite){
        GraphViewData punkt1=null, punkt2=null, punkt3=null, punkt4=null, punkt5=null, punkt6=null;
        double ende = start + breite;

        // Maximalwert der X-Achse die im Graphenlayout angezeigt wird bestimmen (in +2er-Schritten erhoehen!)
        int maxX = (int)ende;

        if((ende - maxX) == 0.5){ // Wenn der Endpunkt des Rechtecks *.5 ist, wird der Maximalwert der X-Koordinate
            // - je nach dem ob das Ergebnis gerade/ungerade ist- um +0.5 oder +1.5 erhöht
            maxX++; // entspricht +0.5, da durch (int)ende 0.5 subtrahiert wurden
            if(maxX%2 == 1){maxX++;}
        }
        else {
            if(maxX%2 == 1){maxX ++;} // Bei ungeradem Endpunkt muss die MaximalAnzeige +1 gerechnet werden
            else {maxX +=2;} // der Endpunkt entspricht der Maximalanzeige -> +2 addieren (fuer bessere Anschauung)
        }



        // Maximalwert der Y-Achse die im Graphenlayout angezeigt wird bestimmen (in +3er-Schritten erhoehen)

        int maxY = (int) amplitude;

        if((amplitude - maxY) == 0.5){
            maxY++;

            if(maxY%3 != 0){ // Wenn der naechsthoehrere Wert durch +0.5 nicht durch 3 teilbar war, wird hier der
                // naechstgroessere durch-3-teilbare Wert bestimmt
                maxY += 3-maxY%3;
            }
        } else{
            if(maxY%3 != 0){ // Anders als bei dem x-Maximalwert wird muss auf der Y-Achse nicht zwangsweise freier
                // Platz nach oben sein
                maxY += 3-maxY%3;
            }

        }

        graph.setViewPort(0,maxX);
        graph.setManualYAxisBounds(maxY,0);


        punkt1 = new GraphViewData(0,0);
        punkt2 = new GraphViewData(start,0);
        punkt3 = new GraphViewData(start,amplitude);
        punkt4 = new GraphViewData(ende,amplitude);
        punkt5 = new GraphViewData(ende,0);
        punkt6 = new GraphViewData(maxX,0);

        GraphViewSeries graphDaten = new GraphViewSeries(  "", new GraphViewSeriesStyle(Color.rgb(200, 50, 00),4),
                new GraphViewData[] {
                        punkt1, punkt2, punkt3, punkt4, punkt5, punkt6
                });
        graph.removeAllSeries();
        graph.addSeries(graphDaten);
        return graph;

    }


}
