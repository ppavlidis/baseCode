/*
 * The baseCode project
 *
 * Copyright (c) 2011 University of British Columbia
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package ubic.basecode.math;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.io.reader.DoubleMatrixReader;
import cern.colt.list.DoubleArrayList;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;

/**
 * @author pavlidis
 * 
 */
public class MultipleTestCorrectionTest {

    DoubleMatrix<String, String> mat;

    private DoubleArrayList values;

    /*
     * (non-Javadoc)
     *
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {

        DoubleMatrixReader dmr = new DoubleMatrixReader();
        mat = dmr.read( this.getClass().getResourceAsStream( "/data/multtest.test.randord.txt" ) );
        values = new DoubleArrayList( mat.getColumnByName( "rawp" ) );
        values.trimToSize();
    }

    /**
     * @throws Exception
     */
    @Test
    public void testBenjaminiHochberg() throws Exception {
        DoubleArrayList actualResult = MultipleTestCorrection.benjaminiHochberg( values );
        DoubleArrayList expected = new DoubleArrayList( mat.getColumnByName( "BH" ) );
        for ( int i = 0; i < actualResult.size(); i++ ) {
            assertEquals( "At " + i, expected.get( i ), actualResult.get( i ), expected.get( i ) / 10.0 );
        }
    }

    /*
     * Test method for 'basecode.math.MultipleTestCorrection.BenjaminiHochbergCut(DoubleArrayList, double)'
     */
    @Test
    public void testBenjaminiHochbergCut() {
        double actualResult = MultipleTestCorrection.benjaminiHochbergCut( values, 0.01 );
        double expectedResult = 0.0018;
        assertEquals( expectedResult, actualResult, 0.00001 );
    }

    @Test
    public void testBenjaminiHochbergM() {
        DoubleMatrix1D actualResult = MultipleTestCorrection.benjaminiHochberg( new DenseDoubleMatrix1D( values
                .elements() ) );
        DoubleArrayList expected = new DoubleArrayList( mat.getColumnByName( "BH" ) );
        for ( int i = 0; i < actualResult.size(); i++ ) {
            assertEquals( "At " + i, expected.get( i ), actualResult.get( i ), expected.get( i ) / 10.0 );
        }
    }

    /*
     * Test method for 'basecode.math.MultipleTestCorrection.BenjaminiYekuteliCut(DoubleArrayList, double)'
     */
    @Test
    public void testBenjaminiYekuteliCut() {
        double actualResult = MultipleTestCorrection.BenjaminiYekuteliCut( values, 0.01 );
        double expectedResult = 0.00013;
        assertEquals( expectedResult, actualResult, 0.00001 );
    }

    /**
     * @throws Exception
     */
    @Test
    public void testBonferroniCut() throws Exception {
        double actualResult = MultipleTestCorrection.BonferroniCut( values, 0.01 );
        double expectedResult = 3.30E-06;
        assertEquals( expectedResult, actualResult, 0.00001 );
    }

}
