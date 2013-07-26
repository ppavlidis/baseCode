/*
 * The Gemma21 project
 * 
 * Copyright (c) 2007 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package ubic.basecode.ontology.providers;

import ubic.basecode.util.Configuration;

/**
 * Holds a copy of the OBO Disease Ontology.
 * 
 * @author klc
 * @version $Id$
 */
public class DiseaseOntologyService extends AbstractOntologyMemoryBackedService {

    private static final String DISEASE_ONTOLOGY_URL = "url.diseaseOntology";

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.ontology.AbstractOntologyService#getOntologyName()
     */
    @Override
    protected String getOntologyName() {
        return "diseaseOntology";
    }

    @Override
    protected String getOntologyUrl() {
        return Configuration.getString( DISEASE_ONTOLOGY_URL );
    }

}
