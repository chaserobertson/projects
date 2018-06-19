package plugin;

import debugger.Debugger;
import login.Login;
import model.facade.server.ServerModelFacade;
import model.facade.server.ServerModelListStorage;
import persistance.*;
import persistance.MockPersistance.MockPersistanceFactory;
import persistance.interfaces.IPersistanceFactory;
import persistance.interfaces.IPersistanceProvider;

import java.io.*;
import java.net.*;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Created by tsmit on 11/28/2016.
 */
public class PluginManager {

    private static final String CONFIG_FILE = "lib/plugins.conf";
    private static final String JAR_FILE = "persistence.jar";

    private static PluginManager instance;
    private Set<String> persistenceTypes;

    public static PluginManager getInstance() {
        if(instance == null) instance = new PluginManager();
        return instance;
    }

    private PluginManager() {
        persistenceTypes = new HashSet<>();
        try {
            Scanner scanner = new Scanner(new FileReader(CONFIG_FILE));
            while(scanner.hasNext()) {
                persistenceTypes.add(scanner.nextLine());
            }
        } catch (Exception e) {
            Debugger.LogMessage("Missing configuration file.");
            e.printStackTrace();
        }
    }

    public void setPersistence(String persistenceType, int numCommands, boolean wipeStorage) {
        IPersistanceProvider provider;
        IPersistanceFactory factory = null;

        for(String currentType : persistenceTypes) {
            if (persistenceType.equals(currentType)) factory = getPluginFactory(currentType);
        }
        if(factory == null) {
            factory = new MockPersistanceFactory();
            Debugger.LogMessage("Invalid persistence type. Valid persistence types are :"+persistenceTypes.toString());
            Debugger.LogMessage("Server will default to mock persistence.");
        }
        factory.createDatabase();
        provider = factory.createPersistanceProvider(factory.createUserDao(), factory.createGameDao(), new ServerModelFacade("not real",-1,null));
        provider.setWriteModelDelay(numCommands);
        ProxyPersistanceProvider.setPersistanceProvider(provider);
        if(wipeStorage) {
            Debugger.LogMessage("Games and users reset");
            ProxyPersistanceProvider.getInstance().resetGames();
            ProxyPersistanceProvider.getInstance().resetUsers();
        }
        ServerModelListStorage.buildFromPersistance(ProxyPersistanceProvider.getInstance());
        Login.getInstance().loadFromPersistance(ProxyPersistanceProvider.getInstance());
    }

    private IPersistanceFactory getPluginFactory(String className) {
        try {
            File libs = new File("lib");
            File[] jars = libs.listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.getName().toLowerCase().endsWith(JAR_FILE);
                }
            });

            URL[] urls = new URL[jars.length];
            for (int i=0; i<jars.length; i++) {
                urls[i] = jars[i].toURI().toURL();
            }
            ClassLoader uc = new URLClassLoader(urls, this.getClass().getClassLoader());

            Class<?> clazz = Class.forName(className, false, uc);
            Object obj = clazz.newInstance();
            return (IPersistanceFactory)obj;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null;
    }
}
