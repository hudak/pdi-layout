/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.pentaho.community.di.util;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import org.eclipse.swt.graphics.Rectangle;
import org.pentaho.di.core.gui.GUIPositionInterface;
import org.pentaho.di.core.EngineMetaInterface;
import org.pentaho.di.core.gui.Point;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.ui.spoon.AbstractGraph;
import org.pentaho.di.ui.spoon.Spoon;


public class LayoutUtils {

  public static void applyGraphToMeta( Graph g ) {

    if ( g != null ) {

      for ( Vertex v : g.getVertices() ) {
        GUIPositionInterface meta = v.getProperty( GraphUtils.PROPERTY_REF );
        Point originaLocation = meta.getLocation();
        int newX = v.getProperty( GraphUtils.PROPERTY_X );
        int newY = v.getProperty( GraphUtils.PROPERTY_Y );

        boolean changed = ( originaLocation.x != newX || originaLocation.y != newY );
        if ( changed ) {
          meta.setLocation( newX, newY );
          ( (StepMeta) meta ).setChanged( true );
        }
      }
    }
    getActiveGraph().redraw();
  }

  /**
   * This method iterates over the graph looking for x- and y-coordinates that are negative. Using the minimum values
   * for x and y, the graph is shifted to the left and down in order to ensure that all steps/entries are visible on
   * the canvas.
   * <p/>
   * This should be called after all layout applications are performed.
   *
   * @param g The graph to update
   */
  public static void fitToBounds( Graph g ) {
    int minX = 0;
    int minY = 0;

    if ( g != null ) {

      for ( Vertex v : g.getVertices() ) {
        int x = v.getProperty( GraphUtils.PROPERTY_X );
        int y = v.getProperty( GraphUtils.PROPERTY_Y );
        if ( x < minX ) {
          minX = x;
        }
        if ( y < minY ) {
          minY = y;
        }
      }

      // Take absolute value and apply margins to get offset(s) to add
      minX = Math.abs( minX );
      minY = Math.abs( minY );

      // Update all vertices
      for ( Vertex v : g.getVertices() ) {
        GUIPositionInterface meta = v.getProperty( GraphUtils.PROPERTY_REF );
        Point originaLocation = meta.getLocation();
        int newX = (int) v.getProperty( GraphUtils.PROPERTY_X ) + minX;
        int newY = (int) v.getProperty( GraphUtils.PROPERTY_Y ) + minY;

        boolean changed = ( originaLocation.x != newX || originaLocation.y != newY );
        if ( changed ) {
          meta.setLocation( newX, newY );
          ( (StepMeta) meta ).setChanged( true );
        }
      }
    }
    getActiveGraph().redraw();
  }

  public static Point getGraphDimensions( EngineMetaInterface engineMeta ) {
    Rectangle r = Spoon.getInstance().getActiveTransGraph().getClientArea();
    return new Point( r.width, r.height );
  }

  public static AbstractGraph getActiveGraph() {
    return Spoon.getInstance().getActiveTransGraph();
  }
}
