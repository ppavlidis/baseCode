package baseCode.algorithm.learning.unsupervised.cluster;

import java.util.TreeSet;

import baseCode.common.Distanceable;
import baseCode.dataStructure.tree.BinaryTree;
import baseCode.dataStructure.tree.BinaryTreeNode;

import cern.colt.list.ObjectArrayList;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;

/**
 * aminal
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class HierarchicalClusterer implements ClusteringAlgorithm {

   int STORED = 10;

   ClusterNode nodes;

   DoubleMatrix2D distances;

   ObjectArrayList objects;

   /**
    * foo
    */
   public HierarchicalClusterer( ObjectArrayList c ) {
      this.objects = c;
      distances = new DenseDoubleMatrix2D( c.size(), c.size() );
   }

   /*
    * (non-Javadoc)
    * 
    * @see baseCode.algorithm.learning.unsupervised.cluster.Algorithm#Run()
    */
   public void run() {

      // keep a running list of the best distances.
      TreeSet closestPairs = new TreeSet();
      AverageLinkageDistancer ald = new AverageLinkageDistancer();
      closestPairs.add( new ClusterNode( Double.MAX_VALUE, null, null, ald ) );

      // compute all the distances. Keep track of the best distances.
      for ( int i = 0; i < objects.size(); i++ ) {
         Distanceable elA = ( Distanceable ) objects.elements()[i];
         for ( int j = i + 1; j < objects.size(); j++ ) {
            Distanceable elB = ( Distanceable ) objects.elements()[j];
            double d = elA.distanceTo( elB );
            distances.setQuick( i, j, d );
            distances.setQuick( j, i, d );
            if ( closestPairs.first() == null
                  || d < ( ( ClusterNode ) closestPairs.first() ).getDistance() ) {
               closestPairs.add( new ClusterNode( d, elA, elB, ald ) );
               if ( closestPairs.size() > STORED ) {
                  closestPairs.remove( closestPairs.last() );
               }
            }
         }
      }

      nodes = ( ClusterNode ) closestPairs.first();  
      
      
      // measure the distance of the new cluster to all the others.
      for ( int i = 0; i < objects.size(); i++ ) {
         Distanceable elA = ( Distanceable ) objects.elements()[i];
         if ( elA.isVisited() ) {
            continue;
         }
         double d = nodes.distanceTo(elA);
         
      }

      
      objects.add( closestPairs.first() );
      ( ( ClusterNode ) closestPairs.first() ).getFirstThing().mark();
      ( ( ClusterNode ) closestPairs.first() ).getSecondThing().mark();
      closestPairs.remove( closestPairs.first() );

      // now we can iterate over the collection, adding nodes and marking their contents after added to the tree.
      for ( int i = 0; i < objects.size(); i++ ) {
         Distanceable elA = ( Distanceable ) objects.elements()[i];
         if ( elA.isVisited() ) {
            continue;
         }
         for ( int j = i + 1; j < objects.size(); j++ ) {
            Distanceable elB = ( Distanceable ) objects.elements()[j];
            if ( elB.isVisited() ) {
               continue;
            }

            double d = distances.getQuick( i, j );
            if ( closestPairs.first() == null
                  || d < ( ( ClusterNode ) closestPairs.first() ).getDistance() ) {
               closestPairs.add( new ClusterNode( d, elA, elB, ald ) );
               if ( closestPairs.size() > STORED ) {
                  closestPairs.remove( closestPairs.last() );
               }
            }
         }
      }
   }

   /**
    * @return
    */
   public Object[] getResults() {
      // TODO Auto-generated method stub
      return null;
   }

}

/* just a helper to store the infor about a cluster node. */

class ClusterNode extends Distanceable {
   private double distance;
   private Distanceable firstThing;
   private Distanceable secondThing;
   private Distancer distancer;

   /**
    * @param distance
    * @param firstThing
    * @param secondThing
    */
   public ClusterNode( double distance, Distanceable firstThing,
         Distanceable secondThing, Distancer distancer ) {
      super();
      this.distance = distance;
      this.firstThing = firstThing;
      this.secondThing = secondThing;
      this.distancer = distancer;
      ;
   }

   public double getDistance() {
      return distance;
   }

   public void setDistance( double distance ) {
      this.distance = distance;
   }

   public Distanceable getFirstThing() {
      return firstThing;
   }

   public void setFirstThing( Distanceable firstThing ) {
      this.firstThing = firstThing;
   }

   public Distanceable getSecondThing() { // this could be another cluster node.
      return secondThing;
   }

   public void setSecondThing( Distanceable secondThing ) {
      this.secondThing = secondThing;
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Comparable#compareTo(java.lang.Object)
    */
   public int compareTo( Object o ) {

      ClusterNode other = ( ClusterNode ) o;

      if ( other.getDistance() > distance ) {
         return 1;
      } else if ( other.getDistance() < distance ) {
         return -1;
      }
      return 0;
   }

   public boolean equals( ClusterNode other ) {
      if ( other.distance == distance ) {
         return true;
      }
      return false;
   }

   public int hashCode() {
      return new Double( distance ).hashCode();
   }

   /*
    * (non-Javadoc)
    * 
    * @see baseCode.common.Distanceable#distanceTo(baseCode.common.Distanceable)
    */
   public double distanceTo( Distanceable a ) {
      return distancer.distance( this, a );
   }
}