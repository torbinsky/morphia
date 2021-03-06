/**
 * Copyright (C) 2010 Olafur Gauti Gudmundsson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.torbinsky.morphia.testdaos;

import com.github.torbinsky.morphia.Morphia;
import com.github.torbinsky.morphia.dao.BasicDAO;
import com.github.torbinsky.morphia.testmodel.Hotel;
import com.mongodb.Mongo;

/**
 *
 * @author Olafur Gauti Gudmundsson
 */
public class HotelDAO extends BasicDAO<Hotel,String> {

    public HotelDAO( Morphia morphia, Mongo mongo ) {
        super(mongo, morphia, "morphia_test");
    }
}
