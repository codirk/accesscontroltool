package biz.netcentric.cq.tools.actool.configReader;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.RepositoryException;

import org.junit.Test;

import biz.netcentric.cq.tools.actool.authorizableutils.AuthorizableConfigBean;
import biz.netcentric.cq.tools.actool.installationhistory.AcInstallationHistoryPojo;
import biz.netcentric.cq.tools.actool.validators.exceptions.AcConfigBeanValidationException;

/** Tests the YamlConfigurationMerger
 * 
 * @author Roland Gruber */
public class YamlConfigurationMergerTest {

    @Test
    public void testMemberGroups() throws IOException, RepositoryException, AcConfigBeanValidationException {
        final String config = YamlConfigReaderTest.getTestConfigAsString("test-membergroups.yaml");
        final ConfigReader reader = new YamlConfigReader();
        final ConfigurationMerger merger = new YamlConfigurationMerger();
        final Map<String, String> configs = new HashMap<String, String>();
        configs.put("/etc/config", config);
        final List results = merger.getMergedConfigurations(configs, mock(AcInstallationHistoryPojo.class), reader);
        final Map<String, Set<AuthorizableConfigBean>> groups = (Map<String, Set<AuthorizableConfigBean>>) results.get(0);
        final AuthorizableConfigBean groupA = groups.get("groupA").iterator().next();
        assertEquals(3, groupA.getMemberOf().length);
        final AuthorizableConfigBean groupB = groups.get("groupB").iterator().next();
        assertEquals(2, groupB.getMemberOf().length);
        final AuthorizableConfigBean groupC = groups.get("groupC").iterator().next();
        final AuthorizableConfigBean groupD = groups.get("groupD").iterator().next();
    }

}
