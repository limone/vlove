package vlove.web.wicket;

import org.apache.wicket.Component;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

public class OutputMarkupIdAlwaysVisitor implements IVisitor<Component, Void> {
  @Override
  public void component(Component object, IVisit<Void> visit) {
    object.setOutputMarkupId(true);
  }
}