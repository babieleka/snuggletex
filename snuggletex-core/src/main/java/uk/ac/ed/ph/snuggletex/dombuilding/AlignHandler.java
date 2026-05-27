package uk.ac.ed.ph.snuggletex.dombuilding;

import org.w3c.dom.Element;
import uk.ac.ed.ph.snuggletex.internal.DOMBuilder;
import uk.ac.ed.ph.snuggletex.internal.SnuggleParseException;
import uk.ac.ed.ph.snuggletex.tokens.CommandToken;
import uk.ac.ed.ph.snuggletex.tokens.EnvironmentToken;
import uk.ac.ed.ph.snuggletex.tokens.FlowToken;

import java.util.List;

/**
 * Handles the LaTeX <code>align</code> environment.
 * <p>
 * This can only be used in MATH mode and generates a <code>mtable</code> as a result.
 *
 * @version $Revision$
 */
public final class AlignHandler implements EnvironmentHandler {

  private final List<String> alignmentsFirstThreeColumns = List.of("right", "center", "left");

  public void handleEnvironment(DOMBuilder builder, Element parentElement, EnvironmentToken token)
          throws SnuggleParseException {
    int[] geometry = TabularHandler.computeTableDimensions(token.getContent());
    int numColumns = geometry[1];

    Element mtableElement = builder.appendMathMLElement(parentElement, "mtable");
    for (FlowToken rowToken : token.getContent()) {
      Element mtrElement = builder.appendMathMLElement(mtableElement, "mtr");
      List<FlowToken> columns = ((CommandToken) rowToken).getArguments()[0].getContents();

      for (int i = 0; i < columns.size(); i++) {
        FlowToken columnToken = columns.get(i);
        Element mtdElement = builder.appendMathMLElement(mtrElement, "mtd");
        String alignment = i < 3 ? alignmentsFirstThreeColumns.get(i) : "left";
        mtdElement.setAttribute("columnalign", alignment);
        builder.handleTokens(mtdElement, ((CommandToken) columnToken).getArguments()[0].getContents(), true);
      }
      /* Add empty <td/> for missing columns */
      for (int i = 0; i < numColumns; i++) {
        builder.appendMathMLElement(mtrElement, "mtd");
      }
    }
  }
}
