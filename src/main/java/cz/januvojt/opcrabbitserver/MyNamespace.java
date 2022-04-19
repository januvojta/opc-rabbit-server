package cz.januvojt.opcrabbitserver;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.milo.opcua.sdk.core.Reference;
import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.api.DataItem;
import org.eclipse.milo.opcua.sdk.server.api.ManagedNamespaceWithLifecycle;
import org.eclipse.milo.opcua.sdk.server.api.MonitoredItem;
import org.eclipse.milo.opcua.sdk.server.nodes.UaFolderNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaVariableNode;
import org.eclipse.milo.opcua.sdk.server.nodes.filters.AttributeFilters;
import org.eclipse.milo.opcua.sdk.server.util.SubscriptionModel;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class MyNamespace extends ManagedNamespaceWithLifecycle implements OpcOutputs {
    private final SubscriptionModel subscriptionModel;
    private final MyOpcVariableBuilder builder;

    private volatile boolean operationStarted;
    private volatile boolean operationFinished;
    private volatile int errorNumber;
    private volatile String errorString = "";
    private volatile String operationState = "";


    public MyNamespace(OpcUaServer server, @Value("${opcua.application.namespace.uri}") String namespaceUri) {
        super(server, namespaceUri);
        this.subscriptionModel = new SubscriptionModel(server, this);

        getLifecycleManager().addLifecycle(subscriptionModel);
        getLifecycleManager().addStartupTask(this::registerNodes);

        builder = new MyOpcVariableBuilder(getNodeContext());
    }

    private void registerNodes() {
        UaFolderNode navServerFolder = createFolderNode(Identifiers.ObjectsFolder, "opcNavServer");
        registerFolderToNode(Identifiers.ObjectsFolder, navServerFolder);

        UaFolderNode outputFolder = createFolderNode(navServerFolder.getNodeId(), "RobotGenericOutput");
        registerFolderToNode(navServerFolder.getNodeId(), outputFolder);

        UaFolderNode inputFolder = createFolderNode(navServerFolder.getNodeId(), "RobotGenericInput");
        registerFolderToNode(navServerFolder.getNodeId(), inputFolder);

        //Input folder nodes
        UaVariableNode programNumberNode = builder
                .setName(getNamespaceIndex(), inputFolder, "programNumber")
                .setDataType(Identifiers.Byte)
                .setReadWriteAccess()
                .setValue(new Variant(0))
                .setAttributeObserver(
                        (node, attributeId, value) -> {
                            //TODO: add hook
                            System.out.println("Value of the node " + node.getBrowseName().getName() + " is: " + ((DataValue) value).getValue().getValue().toString());
                        }
                )
                .build();
        registerNodeToFolder(inputFolder, programNumberNode);

        UaVariableNode dataReadyNode = builder
                .setName(getNamespaceIndex(), inputFolder, "dataReady")
                .setDataType(Identifiers.Boolean)
                .setReadWriteAccess()
                .setValue(new Variant(false))
                .setAttributeObserver(
                        (node, attributeId, value) -> {
                            //TODO: add hook
                            System.out.println("Value of the node " + node.getBrowseName().getName() + " is: " + ((DataValue) value).getValue().getValue().toString());
                        }
                )
                .build();
        registerNodeToFolder(inputFolder, dataReadyNode);

        UaVariableNode shelfIdNode = builder
                .setName(getNamespaceIndex(), inputFolder, "shelfId")
                .setDataType(Identifiers.Int32)
                .setReadWriteAccess()
                .setValue(new Variant(0))
                .setAttributeObserver(
                        (node, attributeId, value) -> {
                            //TODO: add hook
                            System.out.println("Value of the node " + node.getBrowseName().getName() + " is: " + ((DataValue) value).getValue().getValue().toString());
                        }
                )
                .build();
        registerNodeToFolder(inputFolder, shelfIdNode);

        UaVariableNode robotIdNode = builder
                .setName(getNamespaceIndex(), inputFolder, "robotId")
                .setDataType(Identifiers.Int32)
                .setReadWriteAccess()
                .setValue(new Variant(0))
                .setAttributeObserver(
                        (node, attributeId, value) -> {
                            //TODO: add hook
                            System.out.println("Value of the node " + node.getBrowseName().getName() + " is: " + ((DataValue) value).getValue().getValue().toString());
                        }
                )
                .build();
        registerNodeToFolder(inputFolder, robotIdNode);

        //Output folder nodes
        UaVariableNode operationStartedNode = builder
                .setName(getNamespaceIndex(), inputFolder, "operationStarted")
                .setDataType(Identifiers.Boolean)
                .setReadOnlyAccess()
                .setValue(new Variant(operationStarted))
                .setAttributeFilter(AttributeFilters.getValue(
                        ctx -> {
                            log.info("OperationStarted value is : " + operationStarted);
                            return new DataValue(new Variant(operationStarted));
                        }
                ))
                .build();
        registerNodeToFolder(outputFolder, operationStartedNode);

        UaVariableNode operationFinishedNode = builder
                .setName(getNamespaceIndex(), inputFolder, "operationFinished")
                .setDataType(Identifiers.Boolean)
                .setReadOnlyAccess()
                .setValue(new Variant(operationFinished))
                .setAttributeFilter(AttributeFilters.getValue(
                        ctx -> {
                            log.info("OperationFinished value is : " + operationFinished);
                            return new DataValue(new Variant(operationFinished));
                        }
                ))
                .build();
        registerNodeToFolder(outputFolder, operationFinishedNode);

        UaVariableNode errorNumberNode = builder
                .setName(getNamespaceIndex(), inputFolder, "errorNumber")
                .setDataType(Identifiers.Int32)
                .setReadOnlyAccess()
                .setValue(new Variant(errorNumber))
                .setAttributeFilter(AttributeFilters.getValue(
                        ctx -> {
                            log.info("ErrorNumber value is : " + errorNumber);
                            return new DataValue(new Variant(errorNumber));
                        }
                ))
                .build();
        registerNodeToFolder(outputFolder, errorNumberNode);

        UaVariableNode errorStringNode = builder
                .setName(getNamespaceIndex(), inputFolder, "errorString")
                .setDataType(Identifiers.String)
                .setReadOnlyAccess()
                .setValue(new Variant(errorString))
                .setAttributeFilter(AttributeFilters.getValue(
                        ctx -> {
                            log.info("ErrorString value is : " + errorString);
                            return new DataValue(new Variant(errorString));
                        }
                ))
                .build();
        registerNodeToFolder(outputFolder, errorStringNode);

        UaVariableNode operationStateNode = builder
                .setName(getNamespaceIndex(), inputFolder, "operationState")
                .setDataType(Identifiers.String)
                .setReadOnlyAccess()
                .setValue(new Variant(operationState))
                .setAttributeFilter(AttributeFilters.getValue(
                        ctx -> {
                            log.info("OperationState value is : " + operationState);
                            return new DataValue(new Variant(operationState));
                        }
                ))
                .build();
        registerNodeToFolder(outputFolder, operationStateNode);
    }

    private void registerFolderToNode(NodeId predecessor, UaFolderNode folder) {
        getNodeManager().addNode(folder);
        folder.addReference(new Reference(
                folder.getNodeId(),
                Identifiers.Organizes,
                predecessor.expanded(),
                false));
    }

    private void registerNodeToFolder(UaFolderNode folder, UaNode variable) {
        getNodeManager().addNode(variable);
        folder.addOrganizes(variable);
    }

    private UaFolderNode createFolderNode(NodeId predecessor, String name) {
        if (predecessor == null) {
            return new UaFolderNode(
                    getNodeContext(),
                    newNodeId(name),
                    newQualifiedName(name),
                    LocalizedText.english(name));
        } else {
            return new UaFolderNode(
                    getNodeContext(),
                    newNodeId(predecessor.getIdentifier().toString() + name),
                    newQualifiedName(name),
                    LocalizedText.english(name));
        }

    }

    @Override
    public void onDataItemsCreated(List<DataItem> dataItems) {
        this.subscriptionModel.onDataItemsCreated(dataItems);
    }

    @Override
    public void onDataItemsModified(List<DataItem> dataItems) {
        this.subscriptionModel.onDataItemsModified(dataItems);
    }

    @Override
    public void onDataItemsDeleted(List<DataItem> dataItems) {
        this.subscriptionModel.onDataItemsDeleted(dataItems);
    }

    @Override
    public void onMonitoringModeChanged(List<MonitoredItem> monitoredItems) {
        this.subscriptionModel.onMonitoringModeChanged(monitoredItems);
    }


    @Override
    public boolean getOperationStarted() {
        return this.operationStarted;
    }

    @Override
    public void setOperationStarted(boolean value) {
        this.operationStarted = value;
    }

    @Override
    public boolean getOperationFinished() {
        return this.operationFinished;
    }

    @Override
    public void setOperationFinished(boolean value) {
        this.operationFinished = value;
    }

    @Override
    public int getErrorNumber() {
        return this.errorNumber;
    }

    @Override
    public void setErrorNumber(int value) {
        this.errorNumber = value;
    }

    @Override
    public String getErrorString() {
        return this.errorString;
    }

    @Override
    public void setErrorString(String value) {
        this.errorString = value;
    }

    @Override
    public String getOperationState() {
        return this.operationState;
    }

    @Override
    public void setOperationState(String value) {
        this.operationState = value;
    }
}
