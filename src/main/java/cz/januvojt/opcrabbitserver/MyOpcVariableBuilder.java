package cz.januvojt.opcrabbitserver;

import org.eclipse.milo.opcua.sdk.core.AccessLevel;
import org.eclipse.milo.opcua.sdk.server.nodes.AttributeObserver;
import org.eclipse.milo.opcua.sdk.server.nodes.UaFolderNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaNodeContext;
import org.eclipse.milo.opcua.sdk.server.nodes.UaVariableNode;
import org.eclipse.milo.opcua.sdk.server.nodes.filters.AttributeFilter;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;


public class MyOpcVariableBuilder {
    private AttributeFilter attributeFilter = null;
    private AttributeObserver attributeObserver = null;
    private final UaVariableNode.UaVariableNodeBuilder builder;

    public MyOpcVariableBuilder(UaNodeContext context) {
        this.builder = new UaVariableNode.UaVariableNodeBuilder(context);
    }

    public MyOpcVariableBuilder setName(UShort namespaceIndex, UaFolderNode predecessor, String name) {
        this.builder.setNodeId(new NodeId(namespaceIndex, predecessor.getNodeId().getIdentifier().toString() + name))
                .setBrowseName(new QualifiedName(namespaceIndex, name))
                .setDisplayName(LocalizedText.english(name));
        return this;
    }

    public MyOpcVariableBuilder setDataType(NodeId dataType) {
        this.builder.setDataType(dataType);
        return this;
    }

    public MyOpcVariableBuilder setReadWriteAccess() {
        this.builder.setUserAccessLevel(AccessLevel.READ_WRITE)
                .setAccessLevel(AccessLevel.READ_WRITE);
        return this;
    }

    public MyOpcVariableBuilder setReadOnlyAccess() {
        this.builder.setUserAccessLevel(AccessLevel.READ_ONLY)
                .setAccessLevel(AccessLevel.READ_ONLY);
        return this;
    }

    public MyOpcVariableBuilder setValue(Variant value){
        this.builder.setValue(new DataValue(value));
        return this;
    }

    public MyOpcVariableBuilder setAttributeFilter(AttributeFilter attributeFilter) {
        this.attributeFilter = attributeFilter;
        return this;
    }

    public MyOpcVariableBuilder setAttributeObserver(AttributeObserver attributeObserver){
        this.attributeObserver = attributeObserver;
        return this;
    }

    public UaVariableNode build(){
        UaVariableNode node = builder.build();
        if (attributeObserver != null){
            node.addAttributeObserver(attributeObserver);
        }
        if (attributeFilter != null){
            node.getFilterChain().addLast(attributeFilter);
        }
        return node;
    }
}
