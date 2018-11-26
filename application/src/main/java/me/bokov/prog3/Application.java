/*
 *     Chatter - my Programming III. homework assignment
 *     Copyright (C) 2018  Botond János Kovács
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.bokov.prog3;

import me.bokov.prog3.service.ChatServer;
import me.bokov.prog3.service.Database;
import me.bokov.prog3.service.server.ServerConfiguration;
import me.bokov.prog3.ui.ApplicationUIBean;
import me.bokov.prog3.ui.ErrorUIBean;
import me.bokov.prog3.util.Config;
import me.bokov.prog3.util.I18N;
import org.apache.commons.cli.*;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.spi.CDI;
import java.security.SecureRandom;

/**
 * Handles initialization of the CDI container, as well as the core modules of the application, including the
 * application configuration, internationalization (UI translations), database connection and the application's
 * graphical user interface
 */
public class Application {

    private static final String RANDOM_PASSWORD_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static Application INSTANCE = null;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Weld weld;
    private WeldContainer weldContainer;
    private Options cliOptions = null;
    private CommandLine commandLine = null;

    private boolean serverMode = false;
    private boolean enableGui = false;
    private boolean enableDatabase = false;

    /**
     * Creates the singleton instance
     */
    public static void createInstance(String[] args) {
        INSTANCE = new Application();
        INSTANCE.parseCommandLineArguments(args);
        INSTANCE.initialize();
    }

    /**
     * Destroys the singleton instance (before that, it also stops it)
     */
    public static void destroyInstance() {
        if (INSTANCE != null) {

            if (INSTANCE.serverMode) {
                CDI.current().select(ChatServer.class).get()
                        .stop();
            }

            INSTANCE.getWeld().shutdown();
            INSTANCE = null;
        }
    }

    /**
     * @return the singleton instance
     */
    public static Application getInstance() {
        return INSTANCE;
    }

    /**
     * Adds the server-mode related command line arguments to cliOptions
     */
    private void initializeServerOptions() {

        /*
        If the application is started with the --server argument, no GUI will be initialized, instead the application
        will be a headless server instance. In this case, the --port <portnumber> argument is also required, which
        specifies the TCP port that the server will listen for connections on.
         */
        cliOptions.addOption(
                Option.builder()
                        .longOpt("server")
                        .hasArg(false)
                        .build()
        );

        /*
        The --port <portnumber> argument is used to specify the port, that the server will listen on in headless server
        mode.
         */
        cliOptions.addOption(
                Option.builder()
                        .longOpt("port")
                        .hasArg(true)
                        .build()
        );

        /*
        If the --enable-password argument is specified when running in server mode, a password will be required to
        connect to the server. Using the --password <password> argument this password can be specified, but if that
        argument is missing, a 16 character long pseudo-random password will be generated instead, and outputed to the
        application log on startup.
         */
        cliOptions.addOption(
                Option.builder()
                        .longOpt("enable-password")
                        .hasArg(false)
                        .build()
        );

        /*
        Can be used to specify the password for the server when the application is being ran in server mode.
         */
        cliOptions.addOption(
                Option.builder()
                        .longOpt("password")
                        .hasArg(true)
                        .build()
        );

        /*
        The --admin-password <password> argument can be used to specify a remote administration password for the server
        when the application is being ran in server mode. If this argument is missing, a 16 character long pseudo-random
        admin password will be generated, and outputed in the application log on startup.
         */
        cliOptions.addOption(
                Option.builder()
                        .longOpt("admin-password")
                        .hasArg(true)
                        .build()
        );

    }

    /**
     * Initializes the cliOptions variable (Commons CLI Options class)
     */
    private void initializeCliOptions() {

        cliOptions = new Options();

        initializeServerOptions();

        /*
        The --data-directory <dir> can be used to specify a directory to contain application data other than
        <USER_HOME>/.chatter
         */
        cliOptions.addOption(
                Option.builder()
                        .longOpt("data-directory")
                        .hasArg(true)
                        .build()
        );

        /*
        The --no-gui argument is used to disable the application GUI. This argument should only be used when integration
        tests are being ran.
         */
        cliOptions.addOption(
                Option.builder()
                        .longOpt("no-gui")
                        .hasArg(false)
                        .build()
        );


        cliOptions.addOption(
                Option.builder()
                        .longOpt("no-database")
                        .hasArg(false)
                        .build()
        );


    }

    /**
     * Checks for inconsistencies in the command-line arguments (for example server mode, but without a port being
     * specified)
     */
    private void checkCommandLineArgumentConsistency() throws ParseException {

        if (commandLine.hasOption("server")) {

            if (!commandLine.hasOption("port")) {
                throw new ParseException("When running in server mode, the port must be specified via --port <portnumber>!");
            }

        }

    }

    /**
     * Parses command line arguments in the way as they were provided at the application entrypoint by the JVM
     * @param args the command line arguments, as they were provided by the JVM
     */
    private void parseCommandLineArguments(String[] args) {

        initializeCliOptions();

        try {

            commandLine = new DefaultParser().parse(cliOptions, args);

            checkCommandLineArgumentConsistency();

            serverMode = false;
            enableGui = true;
            enableDatabase = true;

            if (commandLine.hasOption("server")) {
                serverMode = true;
                enableGui = false;
            }

            if (commandLine.hasOption("no-gui")) {
                enableGui = false;
            }

            if (commandLine.hasOption("no-database")) {
                enableDatabase = false;
            }

        } catch (ParseException e) {
            new HelpFormatter().printHelp("chatter", cliOptions);
            e.printStackTrace();
            throw new RuntimeException("Could not parse command line arguments!");
        }

    }

    /**
     * Generates a random password
     * @return the random password text
     */
    public String randomPassword() {

        StringBuilder sb = new StringBuilder();

        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < 16; i++) {

            sb.append(
                    RANDOM_PASSWORD_CHARS.charAt(
                            secureRandom.nextInt(RANDOM_PASSWORD_CHARS.length())
                    )
            );

        }

        return sb.toString();

    }

    /**
     * Creates (parses) the server configuration from the command line arguments
     * @return the command line arguments derived server configuration object
     */
    private ServerConfiguration createServerConfigurationFromCommandLineArguments() {

        ServerConfiguration cfg = new ServerConfiguration();

        if (commandLine.hasOption("enable-password")) {

            cfg.setPasswordEnabled(true);
            cfg.setPassword(commandLine.getOptionValue("password", randomPassword()));

        }

        cfg.setAdminPassword(commandLine.getOptionValue("admin-password", randomPassword()));

        cfg.setPort(Integer.parseInt(commandLine.getOptionValue("port")));

        return cfg;

    }

    /**
     * Initializes the core modules of the application
     */
    private void initialize() {

        logger.info("Application initialization started");

        // Create Weld and initialize it
        // After this point, CDI should be available in the application

        weld = new Weld();
        weldContainer = weld.initialize();

        try {

            // Load config
            CDI.current().select(Config.class).get().load();

            if (isEnableDatabase()) {

                try {

                    // Initialize database
                    CDI.current().select(Database.class).get().start();

                } catch (Exception exc) {

                    exc.printStackTrace();
                    enableDatabase = false;

                }

            }

            if (enableGui) {

                logger.info("GUI is enabled");

                // Load i18n
                CDI.current().select(I18N.class).get().load(CDI.current().select(Config.class).get().getUserLocale());

                // Create UI
                CDI.current().select(ApplicationUIBean.class).get().initialize();

            } else {
                logger.info("GUI is disabled");
            }

            if (serverMode) {

                // Running in server mode
                // Server configuration comes from command line arguments

                logger.info("Running in server mode");

                ServerConfiguration serverConfiguration = createServerConfigurationFromCommandLineArguments();

                if (serverConfiguration.isPasswordEnabled()) {
                    logger.info("Server will be password protected, using password '{}'", serverConfiguration.getPassword());
                }
                logger.info("Server will use the following password for remote administration: '{}'", serverConfiguration.getAdminPassword());

                CDI.current().select(ChatServer.class).get()
                        .start(serverConfiguration);

            }

        } catch (Throwable th) {

            logger.error("Error during application initialization", th);

            CDI.current().select(ErrorUIBean.class).get()
                    .showThrowable(th);

            throw th;

        }

        logger.info("Application initialization finished");

    }

    /**
     * @return the Weld SE instance
     */
    public Weld getWeld() {
        return weld;
    }

    /**
     * @return the WELD SE CDI container
     */
    public WeldContainer getWeldContainer() {
        return weldContainer;
    }

    /**
     * @return the parsed command line arguments (parsed by Commons CLI)
     */
    public CommandLine getCommandLine() {
        return commandLine;
    }

    public boolean isEnableDatabase() {
        return enableDatabase;
    }
}
