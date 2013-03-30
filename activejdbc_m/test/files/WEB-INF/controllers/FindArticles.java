package controllers;

import ca.simplegames.micro.Controller;
import ca.simplegames.micro.MicroContext;
import ca.simplegames.micro.SiteContext;
import ca.simplegames.micro.controllers.ControllerException;
import ca.simplegames.micro.extensions.ActivejdbcExtension;
import models.Article;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2013-03-10 3:48 PM)
 */
public class FindArticles implements Controller {
    @Override
    public void execute(MicroContext context, Map map) throws ControllerException {
        SiteContext site = context.getSiteContext();
        ActivejdbcExtension ajExtension = (ActivejdbcExtension) site.get("activejdbc_m");
        if(ajExtension!=null){
            try {
                ajExtension.before();
                List<Article> articles = Article.findAll();
                context.put("totalArticles", articles.size());
                context.put("article", articles.get(0).toMap());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                ajExtension.after();
            }
        }else{
            throw new RuntimeException("The ActiveJDBC extension is not enabled");
        }
    }
}
