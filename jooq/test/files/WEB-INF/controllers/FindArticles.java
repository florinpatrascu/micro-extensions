package controllers;

import ca.simplegames.micro.Controller;
import ca.simplegames.micro.MicroContext;
import ca.simplegames.micro.SiteContext;
import ca.simplegames.micro.controllers.ControllerException;
import ca.simplegames.micro.extensions.JOOQExtension;
import models.tables.Articles;
import org.jooq.Record;
import org.jooq.Result;

import java.util.Map;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2014-04-01)
 */
public class FindArticles implements Controller {

  @Override
  public void execute(MicroContext context, Map map) throws ControllerException {
    SiteContext site = context.getSiteContext();
    JOOQExtension jOOQ = (JOOQExtension) site.get("jOOQ");

    if (jOOQ != null) {
      JOOQExtension.MDSL mdsl =null;
      try {

        mdsl = jOOQ.getDSL();
        Result<Record> articles = mdsl.getCreate().select().from(Articles.ARTICLES).fetch();
        context.put("totalArticles", articles.size());
        context.put("article", articles.get(0));

      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        jOOQ.after(mdsl);
      }
    } else {
      throw new RuntimeException("The jOOQ extension is not enabled");
    }
  }
}
