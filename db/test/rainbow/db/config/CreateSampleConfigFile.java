package rainbow.db.config;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import rainbow.db.config.Config;
import rainbow.db.config.Logic;
import rainbow.db.config.Physic;

public class CreateSampleConfigFile {

    public static void main(String[] args) throws FileNotFoundException, Exception {
        Config config = new Config();
        Physic physic = new Physic();
        physic.setId("default");
        physic.setDriverClass("org.h2.Driver");
        physic.setJdbcUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        physic.setUsername("rainbow");
        physic.setPassword("rainbow");
        config.getPhysics().add(physic);

        Logic logic = new Logic();
        logic.setId("default");
        logic.setPhysic("default");
        config.getLogics().add(logic);
        Logic logic1 = new Logic();
        logic1.setId("vv");
        logic1.setPhysic("default");
        logic1.setModel("ss");
        config.getLogics().add(logic1);

        Config.getXmlBinder().marshal(config, new FileOutputStream("database.xml"));
    }
}
