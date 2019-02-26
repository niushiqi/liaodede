package com.dyyj.idd.chatmore.weiget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.dyyj.idd.chatmore.utils.DeviceUtils;

import java.util.ArrayList;
import java.util.List;

public class LevelProgressView extends ViewGroup {

    private Bitmap iconBitmap;

    private int radiusBig, radiusCenter, radiusSmall;
    private int lineWidth, lineHeight;

    private Paint paintProgress, paintCenter, paintSmall, paintLine;

    private int mWidth, mHeight;
    private int nodeNums = 4;
    private List<Node> nodeList = new ArrayList<>();
    private List<RectF> rectList = new ArrayList<>();
    private List<LineNode> lineList = new ArrayList<>();
    private int realW;
    private int nodeOkNums = 0;
    private double progress;

    public LevelProgressView(Context context) {
        super(context);
        initView(context);
    }

    public LevelProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LevelProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        setWillNotDraw(false);

        radiusBig = (int) DeviceUtils.dp2px(getResources(), 11) / 2;
        radiusCenter = (int) (radiusBig - DeviceUtils.dp2px(getResources(), 1));
        radiusSmall = (int) (radiusCenter - DeviceUtils.dp2px(getResources(), 1));

        lineWidth = (int) (radiusCenter * Math.cos(20 * Math.PI / 180));
        lineHeight = (int) (radiusCenter * Math.sin(20 * Math.PI / 180));

//        iconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_ok_red);

        paintProgress = new Paint();
        paintProgress.setColor(getResources().getColor(android.R.color.holo_red_light));
        paintProgress.setAntiAlias(true);
        paintProgress.setStrokeCap(Paint.Cap.ROUND);
        paintProgress.setStrokeWidth(lineHeight * 2);

        paintCenter = new Paint();
        paintCenter.setColor(getResources().getColor(android.R.color.darker_gray));
        paintCenter.setAntiAlias(true);
        paintCenter.setStyle(Paint.Style.STROKE);
        paintCenter.setStrokeWidth(DeviceUtils.dp2px(getResources(), 1));

        paintSmall = new Paint();
        paintSmall.setColor(getResources().getColor(android.R.color.darker_gray));
        paintSmall.setAntiAlias(true);

        paintLine = new Paint();
        paintLine.setColor(getResources().getColor(android.R.color.darker_gray));
        paintLine.setAntiAlias(true);
        paintLine.setStrokeWidth(DeviceUtils.dp2px(getResources(), 1));

        if (isInEditMode()) {
            return;
        }
    }

    /**
     * 设置节点图片资源
     * @param resourceId
     */
    public void setNodeSrc(int resourceId) {
        iconBitmap = BitmapFactory.decodeResource(getResources(), resourceId);
    }

    /**
     * 设置需要显示的节点数量
     * @param nums
     */
    public void setNodeOkNums(int nums) {
        this.nodeOkNums = nums;
    }

    /**
     * 设置进度条颜色
     * @param color
     */
    public void setProgressColor(int color) {
        paintProgress.setColor(color);
    }

    /**
     * 设置进度条长度
     * @param percent
     */
    public void setProgress(double percent) {
        if (percent > 1) {
            percent /= 100;
        }
        progress = percent;
//        setNodeOkNums(1);
//        if (progress >= 0.33) {
//            setNodeOkNums(2);
//        }
//        if (progress >= 0.66) {
//            setNodeOkNums(3);
//        }
//        if (progress >= 0.99) {
//            setNodeOkNums(4);
//        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

        realW = mWidth - radiusBig * 2;
        int nodeW = realW / (nodeNums - 1);
        int line = nodeW - lineWidth * 2;

        nodeList.clear();
        rectList.clear();
        lineList.clear();
        for (int i = 0; i < nodeNums; i++) {
            Node node = new Node();
            node.yIndex = mHeight / 2;
            if (i == 0) {
                node.xIndex = radiusBig;

                RectF rectF = new RectF(node.xIndex - radiusCenter, node.yIndex - radiusCenter, node.xIndex + radiusCenter, node.yIndex + radiusCenter);
                rectList.add(rectF);

                LineNode lineNodeTop = new LineNode();
                lineNodeTop.startX = node.xIndex + lineWidth;
                lineNodeTop.startY = node.yIndex - lineHeight;
                lineNodeTop.endX = lineNodeTop.startX + line;
                lineNodeTop.endY = lineNodeTop.startY;
                lineList.add(lineNodeTop);

                LineNode lineNodeBottom = new LineNode();
                lineNodeBottom.startX = node.xIndex + lineWidth;
                lineNodeBottom.startY = node.yIndex + lineHeight;
                lineNodeBottom.endX = lineNodeBottom.startX + line;
                lineNodeBottom.endY = lineNodeBottom.startY;
                lineList.add(lineNodeBottom);

            } else if (i == nodeNums - 1) {
                node.xIndex = mWidth - radiusBig;

                RectF rectF = new RectF(node.xIndex - radiusCenter, node.yIndex - radiusCenter, node.xIndex + radiusCenter, node.yIndex + radiusCenter);
                rectList.add(rectF);
            } else {
                int nodeWidth = realW / (nodeNums - 1);
                node.xIndex = radiusBig + nodeWidth * i;

                RectF rectF = new RectF(node.xIndex - radiusCenter, node.yIndex - radiusCenter, node.xIndex + radiusCenter, node.yIndex + radiusCenter);
                rectList.add(rectF);

                LineNode lineNodeTop = new LineNode();
                lineNodeTop.startX = node.xIndex + lineWidth;
                lineNodeTop.startY = node.yIndex - lineHeight;
                lineNodeTop.endX = lineNodeTop.startX + line;
                lineNodeTop.endY = lineNodeTop.startY;
                lineList.add(lineNodeTop);

                LineNode lineNodeBottom = new LineNode();
                lineNodeBottom.startX = node.xIndex + lineWidth;
                lineNodeBottom.startY = node.yIndex + lineHeight;
                lineNodeBottom.endX = lineNodeBottom.startX + line;
                lineNodeBottom.endY = lineNodeBottom.startY;
                lineList.add(lineNodeBottom);
            }
            nodeList.add(node);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < nodeList.size(); i++) {
            canvas.drawCircle(nodeList.get(i).xIndex, nodeList.get(i).yIndex, radiusSmall, paintSmall);
        }

        for (int i = 0; i < rectList.size(); i++) {
            if (i == 0) {
                canvas.drawArc(rectList.get(i), 20, 320, false, paintCenter);
            } else if (i == rectList.size() - 1) {
                canvas.drawArc(rectList.get(i), 200, 320, false, paintCenter);
            } else {
                canvas.drawArc(rectList.get(i), 20, 140, false, paintCenter);
                canvas.drawArc(rectList.get(i), 200, 140, false, paintCenter);
            }
        }

        for (LineNode lineNode : lineList) {
            canvas.drawLine(lineNode.startX, lineNode.startY, lineNode.endX, lineNode.endY, paintLine);
        }

        for (int i = 0; i < nodeList.size(); i++) {
            if (i == 0) {
                canvas.drawLine(radiusBig, mHeight / 2, (float) (radiusBig + realW * progress), mHeight / 2, paintProgress);
                if (iconBitmap != null) {
                    drawImage(canvas, iconBitmap, 0, 0, radiusBig * 2, radiusBig * 2, 0, 0);
                }
            } else {
                if (i <= nodeOkNums - 1) {
                    if (iconBitmap != null) {
                        drawImage(canvas, iconBitmap, nodeList.get(i).xIndex - radiusBig, 0, radiusBig * 2, radiusBig * 2, 0, 0);
                    }
                }
            }
        }

    }

    public void drawImage(Canvas canvas, Bitmap blt, int x, int y,
                          int w, int h, int bx, int by) {
        Rect src = new Rect();// 图片 >>原矩形
        Rect dst = new Rect();// 屏幕 >>目标矩形

        src.left = bx;
        src.top = by;
        src.right = bx + w;
        src.bottom = by + h;

        dst.left = x;
        dst.top = y;
        dst.right = x + w;
        dst.bottom = y + h;
        // 画出指定的位图，位图将自动--》缩放/自动转换，以填补目标矩形
        // 这个方法的意思就像 将一个位图按照需求重画一遍，画后的位图就是我们需要的了
        canvas.drawBitmap(blt, null, dst, null);
        src = null;
        dst = null;
    }

    class Node {
        public int xIndex;
        public int yIndex;
    }

    class LineNode {
        public int startX;
        public int startY;
        public int endX;
        public int endY;
    }
}
