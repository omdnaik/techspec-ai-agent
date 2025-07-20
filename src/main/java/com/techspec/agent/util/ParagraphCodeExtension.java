import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.html.*;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.util.data.DataHolder;

import java.util.*;

public class ParagraphCodeExtension implements HtmlRenderer.HtmlRendererExtension {
    public static Extension create() {
        return new ParagraphCodeExtension();
    }

    @Override
    public void rendererOptions(final MutableDataHolder options) {}

    @Override
    public void extend(HtmlRenderer.Builder rendererBuilder, String rendererType) {
        rendererBuilder.nodeRendererFactory(ParagraphCodeNodeRenderer::new);
    }

    static class ParagraphCodeNodeRenderer implements NodeRenderer {
        @Override
        public Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
            return Collections.singleton(
                new NodeRenderingHandler<>(FencedCodeBlock.class, this::render)
            );
        }

        private void render(FencedCodeBlock node, NodeRendererContext context, HtmlWriter html) {
            String[] lines = node.getContentChars().toString().split("\\R");
            html.line();
            html.withAttr().tag("div", false, false, () -> {
                for (String line : lines) {
                    html.tag("p").tag("code");
                    html.text(line);
                    html.tag("/code").tag("/p");
                }
            });
            html.line();
        }
    }
}
