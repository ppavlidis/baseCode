/*
 * The basecode project
 * 
 * Copyright (c) 2005 Columbia University
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package ubic.basecode.datafilter;

import ubic.basecode.dataStructure.matrix.DoubleMatrixNamed;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class TestItemLevelFilter extends AbstractTestFilter {

    ItemLevelFilter f = null;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        f = new ItemLevelFilter();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();

    }

    public final void testFilter() {
        f.setLowCut( 0.0 );
        DoubleMatrixNamed result = ( DoubleMatrixNamed ) f.filter( testdata );
        int expectedReturn = 283;
        int actualReturn = result.rows() * result.columns() - result.numMissing();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

}