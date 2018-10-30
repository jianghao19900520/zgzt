package com.zgzt.pos.utils;

import com.landicorp.android.eptapi.DeviceService;
import com.landicorp.android.eptapi.device.Printer;
import com.landicorp.android.eptapi.exception.RequestException;
import com.landicorp.android.eptapi.utils.QrCode;
import com.zgzt.pos.base.BaseApplication;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.landicorp.android.eptapi.utils.QrCode.ECLEVEL_Q;

/**
 * Created by zixing
 * Date 2018/10/30.
 * desc ：
 */

public class PrinterStep {

    // 自定义错误码:接口调用失败 private Context context;
    private Printer.Progress progress;
    private static final int FAIL = 0xff;
    private Printer printer = Printer.getInstance();
    private List<Printer.Step> stepList;

    public void init() {
        stepList = new ArrayList<Printer.Step>();
    }

    public int getPrinterStatus() {
        try {
            return Printer.getInstance().getStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 返回自定义错误码，表示抛出异常
        return FAIL;
    }

    /**
     * 添加文字
     *
     * @param isTitle
     * @param text
     */
    public boolean addText(final boolean isTitle, final String text) {
        if (stepList == null) {
            return false;
        }
        stepList.add(new Printer.Step() {
            @Override
            public void doPrint(Printer printer) throws Exception {
                printer.setAutoTrunc(false);
                Printer.Format format = new Printer.Format();
                if (isTitle) {
                    //居中
                    format.setAscScale(Printer.Format.ASC_SC2x2);//数字大小
                    format.setAscSize(Printer.Format.ASC_DOT24x12);//数字尺寸
                    format.setHzScale(Printer.Format.HZ_SC3x3);//汉字大小
                    format.setHzSize(Printer.Format.HZ_DOT32x24);//汉字尺寸
                    printer.setFormat(format);
                    printer.printMid(text);
                } else {
                    //居左
                    format.setAscScale(Printer.Format.ASC_SC1x1);//数字大小
                    format.setAscSize(Printer.Format.ASC_DOT24x12);//数字尺寸
                    format.setHzScale(Printer.Format.HZ_SC1x1);//汉字大小
                    format.setHzSize(Printer.Format.HZ_DOT24x24);//汉字尺寸
                    printer.setFormat(format);
                    printer.printMixText(format, text);
                }
            }
        });
        return true;
    }

    /**
     * 添加图片
     */
    public boolean addBitmap() {
        if (stepList == null) {
            return false;
        }
        stepList.add(new Printer.Step() {
            @Override
            public void doPrint(Printer printer) throws Exception {
                try {
                    InputStream inputStream = BaseApplication.mContext.getAssets().open("pay.bmp");
                    printer.printImage(Printer.Alignment.LEFT, inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return true;
    }

    /**
     * 添加条形码
     */
    public boolean addBarcode() {
        if (stepList == null) {
            return false;
        }
        stepList.add(new Printer.Step() {
            @Override
            public void doPrint(Printer printer) throws Exception {
                printer.printBarCode("1234567890");
            }
        });
        return true;
    }

    /**
     * 添加快速响应码
     */
    public boolean addQRcode() {
        if (stepList == null) {
            return false;
        }
        stepList.add(new Printer.Step() {
            @Override
            public void doPrint(Printer printer) throws Exception {
                printer.printQrCode(Printer.Alignment.CENTER, new QrCode("Fujian landi Commercial Equipment Co.,Ltd", ECLEVEL_Q),
                        200);
            }
        });
        return true;
    }

    /**
     * 添加分割线
     */
    public boolean feedLine(final int line) {
        if (stepList == null) {
            return false;
        }
        stepList.add(new Printer.Step() {
            @Override
            public void doPrint(Printer printer) throws Exception {
                printer.feedLine(line);
            }
        });
        return true;
    }

    /**
     * 剪纸
     */
    public boolean cutPage() {
        if (stepList == null) {
            return false;
        }
        stepList.add(new Printer.Step() {
            @Override
            public void doPrint(Printer printer) throws Exception {
                printer.cutPaper();
            }
        });
        return true;
    }

    public void startStepPrint() {
        if (stepList == null) {
            return;
        }
        try {
            DeviceService.login(BaseApplication.mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        progress = new Printer.Progress() {
            @Override
            public void doPrint(Printer printer) {
                // never call
            }

            @Override
            public void onFinish(int error) {
                stepList.clear();
                if (error == Printer.ERROR_NONE) {
                    // TODO 打印成功
                } else {
                    // TODO 打印失败
                }
                DeviceService.logout();
            }

            @Override
            public void onCrash() {
                stepList.clear();
            }
        };

        for (Printer.Step step : stepList) {
            progress.addStep(step);
        }
        try {
            progress.start();
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }
}
