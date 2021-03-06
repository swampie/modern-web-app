package com.github.jknack.mwa.wro4j;

import static org.apache.commons.io.FilenameUtils.getBaseName;
import static org.apache.commons.io.FilenameUtils.getPath;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.servlet.ModelAndView;

import ro.isdc.wro.manager.factory.BaseWroManagerFactory;
import ro.isdc.wro.model.group.Group;
import ro.isdc.wro.model.resource.Resource;
import ro.isdc.wro.model.resource.ResourceType;

/**
 * Publish a module attribute that define a css links elements.
 *
 * @author edgar.espina
 * @since 0.1
 */
public class CssExporter extends WroContribution {

  /**
   * The list of script resources.
   */
  public static final String RESOURCES = "css-resources";

  /**
   * The default variable's name.
   */
  public static final String VARIABLE_NAME = "cssLinks";

  /**
   * Creates a new {@link CssExporter} and css links under the
   * variable's name.
   *
   * @param wroManagerFactory The {@link BaseWroManagerFactory}. Required.
   */
  public CssExporter(final BaseWroManagerFactory wroManagerFactory) {
    super(wroManagerFactory);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doContribution(final Group group,
      final ModelAndView modelAndView,
      final Map<String, Group> additionalGroups) throws IOException {
    Map<String, Object> model = modelAndView.getModel();
    String bundleName = group.getName();
    StringBuilder buffer = new StringBuilder();
    if (useCache()) {
      buffer.append(link("/bundle/" + bundleName + ".css?v=" + version));
    } else {
      List<Resource> candidates = group.getResources();
      List<Resource> resources = new ArrayList<Resource>();
      for (Resource resource : candidates) {
        if (resource.getType() == ResourceType.CSS) {
          // If less or sass is used, set the extension to css.
          String uri = "/" + getPath(resource.getUri())
              + getBaseName(resource.getUri()) + ".css";
          // 1. Collect css.
          buffer.append(link(uri));
          resources.add(resource);
        }
      }
      model.put(resourcesVarName(), resources);
    }
    // 2. Publish as a model attribute.
    model.put(varName(), buffer.toString());
    logger.trace("Publishing {}:\n{}", varName(), buffer);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String varName() {
    return VARIABLE_NAME;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String resourcesVarName() {
    return RESOURCES;
  }
}
