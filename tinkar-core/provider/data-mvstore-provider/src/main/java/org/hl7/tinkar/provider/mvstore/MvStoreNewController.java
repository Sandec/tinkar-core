package org.hl7.tinkar.provider.mvstore;

import com.google.auto.service.AutoService;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;
import org.hl7.tinkar.common.service.*;
import org.hl7.tinkar.common.validation.ValidationRecord;
import org.hl7.tinkar.common.validation.ValidationSeverity;

import java.io.File;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@AutoService(DataServiceController.class)
public class MvStoreNewController extends MvStoreController {
    public static String CONTROLLER_NAME = "New MV Store";
    String importDataFileString;
    DataServiceProperty newFolderProperty = new DataServiceProperty("New folder name", false, true);
    MutableMap<DataServiceProperty, String> providerProperties = Maps.mutable.empty();
    {
        providerProperties.put(newFolderProperty, null);
    }

    @Override
    public void setDataUriOption(DataUriOption option) {
        try {
            importDataFileString = option.uri().toURL().getFile();
         } catch (MalformedURLException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void start() {
        if (MVStoreProvider.singleton == null) {
            try {
                File rootFolder = new File(System.getProperty("user.home"), "Solor");
                File dataDirectory  = new File(rootFolder, providerProperties.get(newFolderProperty));
                ServiceProperties.set(ServiceKeys.DATA_STORE_ROOT, dataDirectory);
                new MVStoreProvider();

                ServiceLoader<LoadDataFromFileController> controllerFinder = ServiceLoader.load(LoadDataFromFileController.class);
                LoadDataFromFileController loader = controllerFinder.findFirst().get();
                Future<Integer> loadFuture = (Future<Integer>) loader.load(new File(importDataFileString));
                int count = loadFuture.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public ImmutableMap<DataServiceProperty, String> providerProperties() {
        return providerProperties.toImmutable();
    }

    @Override
    public void setDataServiceProperty(DataServiceProperty key, String value) {
        providerProperties.put(key, value);
    }

    @Override
    public String controllerName() {
        return CONTROLLER_NAME;
    }

    public List<DataUriOption> providerOptions() {
        List<DataUriOption> dataUriOptions = new ArrayList<>();
        File rootFolder = new File(System.getProperty("user.home"), "Solor");
        if (!rootFolder.exists()) {
            rootFolder.mkdirs();
        }
        for (File f : rootFolder.listFiles()) {
            if (isValidDataLocation(f.getName())) {
                dataUriOptions.add(new DataUriOption(f.getName(), f.toURI()));
            }
        }
        return dataUriOptions;
    }

    @Override
    public boolean isValidDataLocation(String name) {
        return name.toLowerCase().endsWith(".zip") && name.toLowerCase().contains("tink");
    }

    @Override
    public ValidationRecord[] validate(DataServiceProperty dataServiceProperty, Object value, Object target) {
        if (newFolderProperty.equals(dataServiceProperty)) {
            File rootFolder = new File(System.getProperty("user.home"), "Solor");
            if (value instanceof String fileName)  {
                if (fileName.isBlank()) {
                    return new ValidationRecord[] { new ValidationRecord(ValidationSeverity.ERROR,
                            "Directory name cannot be blank", target)};
                } else {
                    File possibleFile = new File(rootFolder, fileName);
                    if (possibleFile.exists())  {
                        return new ValidationRecord[] { new ValidationRecord(ValidationSeverity.ERROR,
                                "Directory already exists", target)};
                    }
                }
            }
        }
        return new ValidationRecord[]{};
    }
}