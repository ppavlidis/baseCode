/*
 * The basecode project
 * 
 * Copyright (c) 2006 University of British Columbia
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
package ubic.basecode.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Pavlidis
 * @author Will Braynen
 * @version $Id$
 */
public class FileTools {
    private static Log log = LogFactory.getLog( FileTools.class.getName() );
    protected final static String PNG_EXTENSION = ".png";
    protected final static String GIF_EXTENSION = ".gif";
    protected final static String TXT_EXTENSION = ".txt";
    protected final static String[] XML_EXTENSIONS = { ".XML", ".RDF-XML", ".rdf-xml.gz", ".rdf-xml.zip", ".xml.zip",
            ".xml.gz" };

    protected final static String[] IMAGE_EXTENSIONS = { PNG_EXTENSION, GIF_EXTENSION, "PNG", "GIF", "JPEG", "JPG" };
    protected final static String[] DATA_EXTENSIONS = { TXT_EXTENSION, ".TXT", "txt.gz", "txt.zip", "txt.gzip" };
    // default values
    public final static String DEFAULT_DATA_EXTENSION = TXT_EXTENSION;
    public final static String DEFAULT_IMAGE_EXTENSION = PNG_EXTENSION;
    public final static String DEFAULT_XML_EXTENSION = ".xml";

    /**
     * @param file
     * @throws IOException
     */
    public static void checkPathIsReadableFile( String file ) throws IOException {
        File infile = new File( file );
        if ( !infile.exists() || !infile.canRead() ) {
            throw new IOException( "Could not find file: " + file );
        }
    }

    /**
     * Returns the extension of a file.
     * 
     * @param filename
     * @return
     * @return
     */
    public static String getExtension( String filename ) {
        String extension = null;
        int i = filename.lastIndexOf( '.' );

        if ( i > 0 && i < filename.length() - 1 ) {
            extension = filename.substring( i + 1 ).toLowerCase();
        }
        return extension;
    } // end getExtension

    /**
     * @param filename
     * @return
     */
    public static String getWithoutExtension( String filename ) {
        String[] s = filename.split( "\\." );
        if ( s.length < 2 ) {
            return filename;
        }
        return s[0];
    }

    /**
     * @param filename
     * @param newExtension
     * @return the new filename with the changed extension, but does not modify the <code>filename</code> parameter.
     */
    public static String changeExtension( String filename, String newExtension ) {

        String filenameWithoutExtension = getWithoutExtension( filename );
        return ( filenameWithoutExtension + "." + newExtension );
    } // end getWithChangedExtension

    /**
     * @param filename
     * @return
     */
    public static boolean hasImageExtension( String filename ) {
        for ( int i = 0; i < FileTools.IMAGE_EXTENSIONS.length; i++ ) {
            if ( filename.toUpperCase().endsWith( FileTools.IMAGE_EXTENSIONS[i].toUpperCase() ) ) {
                return true;
            }
        }
        return false;

    } // end hasImageExtension

    /**
     * @param filename
     * @return
     */
    public static boolean hasXMLExtension( String filename ) {
        for ( int i = 0; i < FileTools.XML_EXTENSIONS.length; i++ ) {
            if ( filename.toUpperCase().endsWith( FileTools.XML_EXTENSIONS[i].toUpperCase() ) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param filename
     * @return the new filename with the added extension, but does not modify the <code>filename</code> parameter.
     */
    public static String addImageExtension( String filename ) {
        return ( filename + ( FileTools.DEFAULT_IMAGE_EXTENSION.startsWith( "." ) ? "" : "." ) + FileTools.DEFAULT_IMAGE_EXTENSION );
    }

    /**
     * @param filename
     * @return the new filename with the added extension, but does not modify the <code>filename</code> parameter.
     */
    public static String addDataExtension( String filename ) {
        return ( filename + ( FileTools.DEFAULT_DATA_EXTENSION.startsWith( "." ) ? "" : "." ) + FileTools.DEFAULT_DATA_EXTENSION );
    }

    /**
     * @param dirname directory name
     * @return
     */
    public static boolean testDir( String dirname ) {
        if ( dirname != null && dirname.length() > 0 ) {
            File f = new File( dirname );
            if ( f.isDirectory() && f.canRead() ) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param filename
     * @return
     */
    public static boolean testFile( String filename ) {
        if ( filename != null && filename.length() > 0 ) {
            File f = new File( filename );
            if ( f.isFile() && f.canRead() ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Test whether a File is writeable.
     * 
     * @param file
     * @return
     */
    public static boolean testFile( File file ) {
        if ( file != null ) {
            if ( file.isFile() && file.canRead() ) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param filename
     * @return
     */
    public static boolean isZipped( String filename ) {
        String capfileName = filename.toUpperCase();
        if ( capfileName.endsWith( ".ZIP" ) ) {
            return true;
        }
        return false;
    }

    /**
     * @param fileName
     * @return
     */
    public static boolean isGZipped( String fileName ) {
        String capfileName = fileName.toUpperCase();
        if ( capfileName.endsWith( ".GZ" ) || capfileName.endsWith( ".GZIP" ) ) {
            return true;
        }
        return false;
    }

    /**
     * Open a non-compresed, zipped, or gzipped file. Uses the file name pattern to figure this out.
     * 
     * @param fileName. If Zipped, this only works if there is just one file in the archive.
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static InputStream getInputStreamFromPlainOrCompressedFile( String fileName ) throws IOException,
            FileNotFoundException {
        if ( !FileTools.testFile( fileName ) ) {
            throw new IOException( "Could not read from " + fileName );
        }
        InputStream i;
        if ( FileTools.isZipped( fileName ) ) {
            log.debug( "Reading from zipped file" );
            ZipFile f = new ZipFile( fileName );
            ZipEntry entry = ( ZipEntry ) f.entries().nextElement();
            if ( entry == null ) throw new IOException( "No zip entries" );
            i = f.getInputStream( entry );
        } else if ( FileTools.isGZipped( fileName ) ) {
            log.debug( "Reading from gzipped file" );
            i = new GZIPInputStream( new FileInputStream( fileName ) );
        } else {
            log.debug( "Reading from uncompressed file" );
            i = new FileInputStream( fileName );
        }
        return i;
    }

    /**
     * Given a File object representing a directory, return a collection of File objects representing the files
     * contained in that directory.
     * 
     * @param directory
     * @return
     */
    public static Collection listDirectoryFiles( File directory ) {

        if ( !directory.isDirectory() ) throw new IllegalArgumentException( "Must be a directory" );

        File[] files = directory.listFiles();

        FileFilter fileFilter = new FileFilter() {
            public boolean accept( File file ) {
                return file.isFile();
            }
        };
        files = directory.listFiles( fileFilter );
        return Arrays.asList( files );
    }

    /**
     * Given a File object representing a directory, return a collection of File objects representing the directories
     * contained in that directory.
     * 
     * @param directory
     * @return
     */
    public static Collection listSubDirectories( File directory ) {

        if ( !directory.isDirectory() ) throw new IllegalArgumentException( "Must be a directory" );

        File[] files = directory.listFiles();

        FileFilter fileFilter = new FileFilter() {
            public boolean accept( File file ) {
                return file.isDirectory();
            }
        };
        files = directory.listFiles( fileFilter );
        return Arrays.asList( files );
    }

}