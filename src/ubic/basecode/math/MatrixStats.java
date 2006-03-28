package ubic.basecode.math;

import ubic.basecode.dataStructure.matrix.DoubleMatrix2DNamedFactory;
import ubic.basecode.dataStructure.matrix.DoubleMatrixNamed;
import ubic.basecode.dataStructure.matrix.SparseDoubleMatrix2DNamed;
import cern.colt.function.DoubleFunction;
import cern.colt.list.DoubleArrayList;
import cern.colt.matrix.DoubleMatrix1D;
import cern.jet.math.Functions;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class MatrixStats {

    /**
     * @param data DenseDoubleMatrix2DNamed
     * @return DenseDoubleMatrix2DNamed
     */
    public static DoubleMatrixNamed correlationMatrix( DoubleMatrixNamed data ) {
        DoubleMatrixNamed result = DoubleMatrix2DNamedFactory.dense( data.rows(), data.rows() );

        for ( int i = 0; i < data.rows(); i++ ) {
            DoubleArrayList irow = new DoubleArrayList( data.getRow( i ) );
            for ( int j = i + 1; j < data.rows(); j++ ) {
                DoubleArrayList jrow = new DoubleArrayList( data.getRow( j ) );
                double c = DescriptiveWithMissing.correlation( irow, jrow );
                result.setQuick( i, j, c );
                result.setQuick( j, i, c );
            }
        }
        result.setRowNames( data.getRowNames() );
        result.setColumnNames( data.getRowNames() );

        return result;
    }

    /**
     * @param data DenseDoubleMatrix2DNamed
     * @param threshold only correlations with absolute values above this level are stored.
     * @return SparseDoubleMatrix2DNamed
     */
    public static SparseDoubleMatrix2DNamed correlationMatrix( DoubleMatrixNamed data, double threshold ) {
        SparseDoubleMatrix2DNamed result = new SparseDoubleMatrix2DNamed( data.rows(), data.rows() );

        for ( int i = 0; i < data.rows(); i++ ) {
            DoubleArrayList irow = new DoubleArrayList( data.getRow( i ) );
            for ( int j = i + 1; j < data.rows(); j++ ) {
                DoubleArrayList jrow = new DoubleArrayList( data.getRow( j ) );
                double c = DescriptiveWithMissing.correlation( irow, jrow );
                if ( Math.abs( c ) > threshold ) {
                    result.setQuick( i, j, c );
                    result.setQuick( j, i, c );
                }
            }
        }
        result.setRowNames( data.getRowNames() );
        result.setColumnNames( data.getRowNames() );

        return result;
    }

    /**
     * Find the minimum of the entire matrix.
     * 
     * @param matrix DenseDoubleMatrix2DNamed
     * @return the smallest value in the matrix
     */
    public static double min( DoubleMatrixNamed matrix ) {

        int totalRows = matrix.rows();
        int totalColumns = matrix.columns();

        double min = Double.MAX_VALUE;

        for ( int i = 0; i < totalRows; i++ ) {
            for ( int j = 0; j < totalColumns; j++ ) {
                double val = matrix.getQuick( i, j );
                if ( Double.isNaN( val ) ) {
                    continue;
                }

                if ( val < min ) {
                    min = val;
                }

            }
        }
        if ( min == Double.MAX_VALUE ) {
            return Double.NaN;
        }
        return min; // might be NaN if all values are missing

    } // end min

    /**
     * Compute the maximum value in the matrix.
     * 
     * @param matrix DenseDoubleMatrix2DNamed
     * @return the largest value in the matrix
     */
    public static double max( DoubleMatrixNamed matrix ) {

        int totalRows = matrix.rows();
        int totalColumns = matrix.columns();

        double max = -Double.MAX_VALUE;

        for ( int i = 0; i < totalRows; i++ ) {
            for ( int j = 0; j < totalColumns; j++ ) {
                double val = matrix.getQuick( i, j );
                if ( Double.isNaN( val ) ) {
                    continue;
                }

                if ( val > max ) {
                    max = val;
                }
            }
        }

        if ( max == -Double.MAX_VALUE ) {
            return Double.NaN;
        }

        return max; // might be NaN if all values are missing

    }

    /**
     * Normalize a matrix in place to be a transition matrix. Assumes that values operate such that small values like p
     * values represent closer distances, and the values are probabilities.
     * <p>
     * Each point is first transformed via v' = exp(-v/sigma). Then the values for each node's edges are adjusted to sum
     * to 1.
     * 
     * @param matrixToNormalize
     * @param sigma a scaling factor for the input values.
     */
    public static void rbfNormalize( DoubleMatrixNamed matrixToNormalize, final double sigma ) {

        // define the function we will use.
        DoubleFunction f = new DoubleFunction() {
            public double apply( double value ) {
                return Math.exp( -value / sigma );
            }
        };

        for ( int j = 0; j < matrixToNormalize.rows(); j++ ) { // do each row in turn ...

            DoubleMatrix1D row = matrixToNormalize.viewRow( j );
            row.assign( f );
            double sum = row.zSum();
            row.assign( Functions.div( sum ) );

        }
    }

    /**
     * Normalize a count matrix in place to be a transition matrix. Assumes that the values are defined as "bigger is
     * better"
     * 
     * @param matrixToNormalize
     * @param sigma
     */
    public static void countsNormalize( DoubleMatrixNamed matrixToNormalize, final double sigma ) {

        final double min = MatrixStats.min( matrixToNormalize );
        DoubleFunction f = new DoubleFunction() {
            public double apply( double value ) {
                return value - min + 1;
            }
        };

        for ( int j = 0; j < matrixToNormalize.rows(); j++ ) { // do each row in turn ...
            DoubleMatrix1D row = matrixToNormalize.viewRow( j );
            row.assign( f );
            double sum = row.zSum();
            row.assign( Functions.div( sum ) );
        }
    }

    /**
     * @param input raw double 2-d matrix
     * @return the element-by-element product (not matrix product) of the matrix.
     */
    public static double[][] selfSquaredMatrix( double[][] input ) {
        double[][] returnValue = new double[input.length][];
        for ( int i = 0; i < returnValue.length; i++ ) {
            returnValue[i] = new double[input[i].length];

            for ( int j = 0; j < returnValue[i].length; j++ ) {
                returnValue[i][j] = input[i][j] * input[i][j];
            }

        }
        return returnValue;
    }

    /**
     * @param data
     * @return matrix indicating whether each value in the input matix is NaN.
     */
    public static boolean[][] nanStatusMatrix( double[][] data ) {
        boolean[][] result = new boolean[data.length][];
        for ( int i = 0; i < data.length; i++ ) {
            double[] row = data[i];
            result[i] = new boolean[data[i].length];
            for ( int j = 0; j < row.length; j++ ) {
                result[i][j] = Double.isNaN( data[i][j] );
            }
        }
        return result;
    }

}