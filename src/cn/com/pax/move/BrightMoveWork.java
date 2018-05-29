package cn.com.pax.move;

import cn.com.pax.display.BarcodePanel;
import cn.com.pax.display.mainFrame;
import cn.com.pax.protocol.Protocol;
import cn.com.pax.protocol.SocketToC4;
import cn.com.pax.report.TiaoMaInfo;
import cn.com.pax.sericomm.SerialConnection;
import cn.pax.com.utils.*;

import java.awt.*;
import java.util.List;

public class BrightMoveWork {
    private SerialConnection connection;
    private String ip;
    private int port;

    private String D220ip ;
    private int D220port ;

    //need to modify for brightness
    public static final String MRCOMMAND ="dofone 0 ";
    private String path = "./config/ConSocket.properties";
    private String path1 = "./config/D220CfgSocket.properties";

    public BrightMoveWork(SerialConnection connection) {
        this.connection = connection;
    }

    public boolean  brightWorkTest(boolean isTwoWei, String strType) {
        SocketToC4 socket_C4 = ReadSocketCfg.getSocket(path);
        SocketToC4 socket_D220 = ReadSocketCfg.getSocket(path1);//D220
        if(socket_C4 == null || !socket_C4.openConn()) {
            mainFrame.logger.error("连接机器人失败，请检查配置！!");
            return false;
        }
        if(socket_D220 == null || !socket_D220.openConn()) {
            mainFrame.logger.error("连接手机失败，请检查配置！!");
            socket_C4.closeConn();
            return false;
        }

        ReadProperties readProperties = new ReadProperties();
       // readProperties.ReadDepthProperties();
        int nextDis = Integer.parseInt(readProperties.getOneone_Depth());
        String str1 = readProperties.getBrightOne();
        if(isTwoWei) {
            nextDis = Integer.parseInt(readProperties.getTwotwo_Depth());
            str1 = readProperties.getBrightTwo();
        }

        String sendStr = null;
        int bright = 50;
        int connTimeout  = 3000;

        String []string1 = str1.split(",");
        byte[]receive = new byte[2];

        UtilsTool.updateView(BarcodePanel.currentTestItemField,"手机背光亮度");
        UtilsTool.updateView(BarcodePanel.cardNumField,"1");
        UtilsTool.updateView(BarcodePanel.excutingField,"1");

        try {
            connection.openConnection();
            connection.readExtraData(100, connTimeout);
            connection.clearSendBeforeData();
        } catch (Exception e) {
            e.printStackTrace();
            socket_C4.closeConn();
            socket_D220.closeConn();
            connection.closeConnection();
            return false;
        }

        labelDo:do {
            UtilsTool.updateView(BarcodePanel.barcodePre_contentArea, string1[0]);
            UtilsTool.updateView(BarcodePanel.barcodeNameField, string1[1]);

            sendStr = MRCOMMAND + nextDis;
            boolean isTestPass = false;
            String string = socket_C4.writeReadOneLine(sendStr);
            if(string.contains("null") ) {
                break labelDo;
            }else if (string.contains("ok")){
                //清屏
                receive = Protocol.sendCmd2D220(socket_D220, null);
                if (receive[0]!=0x00) {
                    mainFrame.logger.error(receive[0] + "");
                    break labelDo;
                }
                connection.readExtraData(100);
                connection.clearSendBeforeData();

                UtilsTool.updateView(BarcodePanel.bright_Field, (bright * 2) + "");
                //set lowest brightless
                receive = Protocol.sendCmd2D220(socket_D220, (byte)bright);
                if(receive[0]!=0x00) {
                    mainFrame.logger.error("设置亮度失败，设置值：" + bright + " 返回码：" + receive[0]);
                    break labelDo;
                }
                //display picture
                receive = Protocol.sendCmd2D220(socket_D220, string1[2]);
                if(receive[0]!=0x00) {
                    mainFrame.logger.error(receive[0] + "");
                    break labelDo;
                }

                while(true && StopMove.isCodeRunning()) {
                    byte[]recvBuf = new byte[32];
                    isTestPass = DataMatchUtils.getMoveResultMatch(connection, string1[0], recvBuf, connTimeout);
                    if(isTestPass) {
                        updateUI(bright, string1[0]);
                        // mainFrame.logger.info("moverate:"+rate+""+nextDis+"");
                        //rate += 50;
                        break;
                    }
                    else {
                        bright += 10;
                        if(bright > 250) {
                            break;
                        }
                        UtilsTool.updateView(BarcodePanel.bright_Field, (bright * 2) + "");
                        receive = Protocol.sendCmd2D220(socket_D220, (byte)bright);
                        if(receive[0]!=0x00) {
                            mainFrame.logger.error("设置亮度失败，设置值：" + bright + " 返回码：" + receive[0]);
                            break labelDo;
                        }
                    }
                }
            }else {
                break labelDo;
            }

            if(!StopMove.isCodeRunning()) {
                connection.closeConnection();
                socket_C4.closeConn();
                socket_D220.closeConn();
                return false;
            }

            String rlt = bright <= 50 ? "PASS" : "FAIL";
            UtilsTool.updateArea(BarcodePanel.testResultArea, "手机背光亮度", rlt);
            TiaoMaInfo dis1 = new TiaoMaInfo("手机背光亮度", strType, string1[1], nextDis+"",  bright * 2 +" ", rlt, "");
            BarcodePanel.allList.add(dis1);
            try {
                connection.closeConnection();
                socket_C4.closeConn();
                socket_D220.closeConn();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } while (false);

        try {
            connection.closeConnection();
            socket_C4.closeConn();
            socket_D220.closeConn();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void updateUI(final int bright, final String content){
        try {
            EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    BarcodePanel.barcodeDecodeContentArea.setText(content);
                    BarcodePanel.bright_Field.setText((bright * 2) + "");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setBrightValue(byte bright) {
        SocketToC4 socket_D220 = ReadSocketCfg.getSocket("./config/D220CfgSocket.properties");//D220
        if(socket_D220 == null || !socket_D220.openConn()) {
            mainFrame.logger.error("setBrightValue: 连接手机失败，请检查配置!");
            return;
        }
        byte[]receive = Protocol.sendCmd2D220(socket_D220, (byte)bright);
        if(receive[0]!=0x00) {
            mainFrame.logger.error("setBrightValue: 设置亮度失败，设置值：" + bright + " 返回码：" + receive[0]);
        }
        socket_D220.closeConn();
    }

 //   @SuppressWarnings("rawtypes")
//    private void updateResult(SocketToC4 socket, List testResult) {
//        if (testResult.size()>0) {
//            StringBuilder builder = new StringBuilder();
//            for (int j = 0; j < testResult.size(); j++) {
//                String testResultString =testResult.get(j).toString();
//                if(j == testResult.size() - 1)
//                    builder.append(testResultString + " ");
//                else
//                    builder.append(testResultString + ",");
//            }
//            BarcodePanel.barcodeDecodeContentArea.setText("FAIL");
//            mainFrame.logger.info("#moveRate: "+builder.toString()+" FAIL");
//        }else {
//            BarcodePanel.barcodeDecodeContentArea.setText("PASS");
//            mainFrame.logger.info("#moveRate: "+"PASS");
//
//
//        }
//    }

}

