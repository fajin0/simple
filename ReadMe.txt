在trade合约项目文件根目录执行
mvn clean package
保留文件collections_config.json和target文件夹下的chaincode.jar，其余文件删除

cd ../../../test-network
启动网络
./network.sh up

建立通道channela
./network.sh createChannel -c channela

设置FABRIC_CFG_PATH为指向fabric-samples中的core.yaml文件
export PATH=${PWD}/../bin:$PATH        
export FABRIC_CFG_PATH=$PWD/../config/

打包链码，将/chaincode/trade/打包到当前文件夹下命名为trade.tar.gz，语言为java，别名trade
peer lifecycle chaincode package trade.tar.gz --path ../chaincode/trade/ --lang java --label trade


设置以下环境变量，以Org1管理员的身份操作peer CLI，设置其端口，导入其证书
export CORE_PEER_TLS_ENABLED=true
export CORE_PEER_LOCALMSPID="Org1MSP"
export CORE_PEER_TLS_ROOTCERT_FILE=${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt
export CORE_PEER_MSPCONFIGPATH=${PWD}/organizations/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp
export CORE_PEER_ADDRESS=localhost:7051

安装打包好的链码trade.tar.gz,在peer节点上安装链码
peer lifecycle chaincode install trade.tar.gz

查询当前节点安装的链码，成功的话会看到trade的合约ID
peer lifecycle chaincode queryinstalled

导入合约ID.包ID是链码标签和链码二进制文件的哈希值的组合。每个peer节点将生成相同的包ID。
export CC_PACKAGE_ID=trade1:69de748301770f6ef64b42aa6bb6cb291df20aa39542c3ef94008615704007f3
export CC_PACKAGE_ID=

通过链码定义
peer lifecycle chaincode approveformyorg -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --channelID channela --name trade --version 1.0 --package-id $CC_PACKAGE_ID --sequence 1 --tls --cafile ${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem

节点org2同org1
14 export CORE_PEER_LOCALMSPID="Org2MSP"
   export CORE_PEER_TLS_ROOTCERT_FILE=${PWD}/organizations/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/ca.crt
   export CORE_PEER_TLS_ROOTCERT_FILE=${PWD}/organizations/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/ca.crt
   export CORE_PEER_MSPCONFIGPATH=${PWD}/organizations/peerOrganizations/org2.example.com/users/Admin@org2.example.com/msp
   export CORE_PEER_ADDRESS=localhost:9051
15 peer lifecycle chaincode install trade.tar.gz
16 peer lifecycle chaincode queryinstalled
17 export CC_PACKAGE_ID=trade1:69de748301770f6ef64b42aa6bb6cb291df20aa39542c3ef94008615704007f3    //同上
18 peer lifecycle chaincode approveformyorg -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --channelID channela --name trade --version 1.0 --package-id $CC_PACKAGE_ID --sequence 1 --tls --cafile ${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem

将链码定义提交给通道
19 peer lifecycle chaincode checkcommitreadiness --channelID channela --name trade --version 1.0 --sequence 1 --tls --cafile ${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem --output json


20 peer lifecycle chaincode commit -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --channelID channela --name trade --version 1.0 --sequence 1 --tls --cafile ${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem --peerAddresses localhost:7051 --tlsRootCertFiles ${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt --peerAddresses localhost:9051 --tlsRootCertFiles ${PWD}/organizations/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/ca.crt

确认链码定义已提交给通道,查询指定通道上已经提交的智能合约的信息，包括其版本号、序列号、状态等
21 peer lifecycle chaincode querycommitted --channelID channela --name trade --cafile ${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem

查看容器内网络节点状态
22  docker ps -a
查看输出日志
docker logs <id>

调用智能合约的初始化函数initLedger，生成区块信息
23 peer chaincode invoke -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --tls --cafile ${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C channela -n trade --peerAddresses localhost:7051 --tlsRootCertFiles ${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt --peerAddresses localhost:9051 --tlsRootCertFiles ${PWD}/organizations/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/ca.crt -c '{"function":"initLedger","Args":[]}'

调用智能合约的创建函数createTrade
peer chaincode invoke -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --tls --cafile ${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C channela -n trade --peerAddresses localhost:7051 --tlsRootCertFiles ${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt --peerAddresses localhost:9051 --tlsRootCertFiles ${PWD}/organizations/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/ca.crt -c '{"Args":["createTrade", "20011", "Alice1", "123456"]}'



查询交易信息（可能会出错，因为给的key不对）
peer chaincode query -n mychaincode -c '{"Args":["queryTrade", "10001"]}' -C channela











