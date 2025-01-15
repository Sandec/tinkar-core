/*
 * Copyright © 2015 Integrated Knowledge Management (support@ikm.dev)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.ikm.tinkar.entity.export;

import com.google.auto.service.AutoService;
import dev.ikm.tinkar.common.service.TinkExecutor;

import java.io.File;
import java.util.concurrent.Future;

import static dev.ikm.tinkar.entity.Entity.LOG;

@AutoService(ExportEntitiesController.class)
public class ExportEntitiesController {
    public Future<?> export(File pbFile) {
        if (pbFile.getName().toLowerCase().contains("tinkar-export")) {
            return TinkExecutor.ioThreadPool().submit(new ExportEntitiesToProtobufFile(pbFile));
        } else {
            LOG.info("File type is not of Protobuf type. Running the export but check file.");
            throw new UnsupportedOperationException("Invalid export file type");
        }
    }
}
