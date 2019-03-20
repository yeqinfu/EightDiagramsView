package com.example.daggertest;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 八卦view
 */
public class EightDiagramsView extends View {

    /*填充色*/
    private int greenColor = Color.GREEN;
    private int redColor = Color.RED;

    private float numberTextSize=0;
    private float textSize=0;
    public EightDiagramsView(Context context) {
        super(context);
    }

    public EightDiagramsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadAttr(attrs);
    }

    public EightDiagramsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadAttr(attrs);
    }

    private void loadAttr(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.EightDiagramsView);

        greenColor=typedArray.getColor(R.styleable.EightDiagramsView_colorfill,Color.GREEN);
        numberTextSize=typedArray.getDimension(R.styleable.EightDiagramsView_numberTextSize,sp2px(16));
        textSize=typedArray.getDimension(R.styleable.EightDiagramsView_textSize,sp2px(20));
        // 用完要关闭回收资源，必须的强制性的
        typedArray.recycle();


    }

    /**
     * 方向文字
     */
    private String[] directionText = {"北", "东", "南", "西"};
    /**
     * 方向文字 偏方向
     */
    private String[] directionText2 = {"东北", "东南", "西南", "西北"};
    /**
     * 八卦文字
     */
    private String[] eightDiagramsText = {"乾", "巽", "坎", "艮", "坤", "震", "离", "兑"};
    /**
     * 中间九个数字
     */
    private String[] middleNumber = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};
    /**
     * 数字范围内圆
     */
    private float centerRadius = 5;

    /**
     * 数字范围内圆2
     */
    private float centerRadius2 = 6;

    /**
     * 第一个八卦的半径
     */
    private float eightDiagramsRadius = 8;

    /**
     * 第二个八卦的半径
     */
    private float eightDiagramsRadius2 = 12;

    /**
     * 最外层的文字高度，高度靠外和靠里有一个缝隙，
     */
    private float outTextHeight = 4;
    /**
     * 第三个八卦的半径，也就是最大半径
     * 这个2先代表缝隙空间值
     */
    private float eightDiagramsRadius3 = eightDiagramsRadius2 + outTextHeight + 2;




    private float centerX;
    private float centerY;

    private void init() {
        Log.d("yeqinfu", "==========init==========");
        centerRadius = eightDiagramsHeight / 6;
        centerRadius2 = centerRadius + eightDiagramsHeight / 40;
        eightDiagramsRadius = centerRadius + eightDiagramsHeight / 20;
        eightDiagramsRadius2 = eightDiagramsRadius + eightDiagramsHeight / 8;
        outTextHeight = eightDiagramsHeight / 20;
        eightDiagramsRadius3 = eightDiagramsHeight / 2;
        centerX = eightDiagramsHeight / 2;
        centerY = eightDiagramsHeight / 2;
        numberRadius=eightDiagramsHeight/26;
        numerOffset=numberRadius/5;
        /*初始化点集合*/
        initPointList();
        invalidate();

    }


    /**
     * 极坐标类
     */
    static class Polar {
        double radius;
        double angle;

        public Polar(double radius, double angle) {
            this.radius = radius;
            this.angle = angle;
        }

        public double getRadius() {

            return radius;
        }

        public void setRadius(double radius) {
            this.radius = radius;
        }

        public double getAngle() {
            return angle;
        }

        public void setAngle(double angle) {
            this.angle = angle;
        }

        public Point toPoint() {
            Point point = new Point();
            point.x = (int) (Math.cos(angle) * radius);
            point.y = (int) (Math.sin(angle) * radius);
            return point;
        }
    }


    /**
     * 所有的半径都确定之后，相对应的八卦点集合也都可以确定，这可以初始化
     * 内部勾勒圆型的交汇点集合
     * 第一层八卦图点集合
     * 第二层八卦图点集合
     */
    private List<Point> pointsFirst = new ArrayList<>();
    private List<Point> pointsSecond = new ArrayList<>();
    private List<Point> pointsThird = new ArrayList<>();

    private void printPoint(List<Point> list) {
        for (Point p : list
        ) {
            Log.d("yeqinfu", "---" + p.x + "---" + p.y);
        }

    }

    private  float numberRadius=1;
    private  float numerOffset=1;//两个圆之间的间隙
    private  List<Point> numberPoints=new ArrayList<>();
    private void initPointList() {
        /*d点的方向统一都是12点方向，顺时针存入 所以初始夹角是Math.PI/2 每次减少Math.PI/4*/
        double initAngle = Math.PI / 2 - Math.PI / 8;
        for (int i = 0; i < 8; i++) {
            Polar polar = new Polar(centerRadius2, initAngle);
            pointsFirst.add(polar.toPoint());
            //然后把这个极坐标类的半径增加到第二个半径长度，加入到第二个点集合
            polar.setRadius(eightDiagramsRadius);
            pointsSecond.add(polar.toPoint());
            //继续增加半径，加入到第三个集合
            polar.setRadius(eightDiagramsRadius2);
            pointsThird.add(polar.toPoint());
            //改角度
            initAngle -= Math.PI / 4;

        }
        int length= (int) (numberRadius*2+numerOffset);
        numberPoints.add(new Point(-length,-length));//一号圆位置
        numberPoints.add(new Point(0,-length));//2号圆位置
        numberPoints.add(new Point(length,-length));
        numberPoints.add(new Point(-length,0));//一号圆位置
        numberPoints.add(new Point(0,0));//2号圆位置
        numberPoints.add(new Point(length,0));
        numberPoints.add(new Point(-length,length));//一号圆位置
        numberPoints.add(new Point(0,length));//2号圆位置
        numberPoints.add(new Point(length,length));






    }

    float mWidth, mHeight;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        /**第一层
         * 第一层画基本图形，包括实体圆型，八卦
         * */
        canvas.save();
        drawLevelOne(canvas);
        canvas.restore();


        /**
         * 第二层画中间的九个小圆形
         * 所有文字
         */
        canvas.save();

        drawLevelTwo(canvas);
        canvas.restore();


        /*辅助坐标*/
       // canvas.drawLine(mWidth / 2, 0, mWidth / 2, mHeight, getBlackFillPaint());
       // canvas.drawLine(0, mHeight / 2, mWidth, mHeight / 2, getBlackFillPaint());



    }

    class TextOffset {
        public float offSetX;
        public float offSetY;
    }

    /**
     * 画文字
     *
     * @param canvas
     */
    private void drawLevelTwo(Canvas canvas) {

        // 将坐标系原点移动到画布正中心
        canvas.translate(mWidth / 2, mHeight / 2);
       // canvas.drawCircle(0,0,eightDiagramsRadius2,getGreenStrokePaint());//辅助园可以去掉

        TextOffset offset = null;
        //画北
        canvas.save();
        offset = getTextOffSet(directionText[0]);
        canvas.translate(0, -eightDiagramsRadius2-outTextHeight);

        /*外部 填充边长 设定为底部八卦边长*/
        double width=getDistance(pointsThird.get(0),pointsThird.get(1))*0.8;
        double height=width/2;//高度
        RectF rectF=new RectF();
        rectF.left= (int) -(width/2);
        rectF.top=(int) -(height/2);
        rectF.right=(int) (width/2);
        rectF.bottom=(int) (height/2);

        canvas.drawRoundRect(rectF,(float)height/5,(float)height/5,getGreenFillPaint());
       // canvas.drawRect(rect,getGreenFillPaint());
//        canvas.drawCircle(0,0,10,getBlackFillPaint());
        canvas.drawText(directionText[0], -offset.offSetX, -offset.offSetY, getWriteTextPaint());
        canvas.restore();

        //东
        canvas.save();
        offset = getTextOffSet(directionText[1]);
        canvas.translate(eightDiagramsRadius2 +outTextHeight, 0);

        rectF.left= (int) -(height/2);
        rectF.top=(int) -(width/2);
        rectF.right=(int) (height/2);
        rectF.bottom=(int) (width/2);
        canvas.drawRoundRect(rectF,(float)height/5,(float)height/5,getGreenFillPaint());


        canvas.drawText(directionText[1], 0 -offset.offSetX, 0 - offset.offSetY, getWriteTextPaint());
        canvas.restore();
        //南
        canvas.save();

        offset = getTextOffSet(directionText[2]);
        canvas.translate(0, eightDiagramsRadius2 +outTextHeight);
        rectF.left= (int) -(width/2);
        rectF.top=(int) -(height/2);
        rectF.right=(int) (width/2);
        rectF.bottom=(int) (height/2);
        canvas.drawRoundRect(rectF,(float)height/5,(float)height/5,getGreenFillPaint());

        canvas.drawText(directionText[2], 0 -offset.offSetX, 0 - offset.offSetY, getWriteTextPaint());
        canvas.restore();
        //西
        canvas.save();
        offset = getTextOffSet(directionText[3]);
        canvas.translate(-eightDiagramsRadius2 -outTextHeight, 0);
        rectF.left= (int) -(height/2);
        rectF.top=(int) -(width/2);
        rectF.right=(int) (height/2);
        rectF.bottom=(int) (width/2);
        canvas.drawRoundRect(rectF,(float)height/5,(float)height/5,getGreenFillPaint());

        canvas.drawText(directionText[3], 0 -offset.offSetX, 0 - offset.offSetY, getWriteTextPaint());
        canvas.restore();

      /*  canvas.save();
        offset=getTextOffSet("中");
        canvas.drawText("中",-offset.offSetX,-offset.offSetY,getBlackTextPaint());
        canvas.restore();*/

        /**偏方向*/
        //偏方向字距离根北一致，然后进行角度偏移
        canvas.save();
       // canvas.drawCircle(0,0,10,getBlackFillPaint());
      //  canvas.drawCircle(100,100,50,getBlackFillPaint());

       // canvas.drawCircle(100,100,50,getBlackFillPaint());
        canvas.rotate(45);
        for (String text:directionText2 ) {
            int dis= (int) (eightDiagramsRadius2+outTextHeight);
            rectF.left= (int) -(width/2);
            rectF.top=(int) (height/2)-dis;
            rectF.right=(int) (width/2);
            rectF.bottom=(int) -(height/2)-dis;
            canvas.drawRoundRect(rectF,(float)height/5,(float)height/5,getGreenFillPaint());

            offset = getTextOffSet(text);

            canvas.drawText(text, 0 -offset.offSetX,-offset.offSetY-eightDiagramsRadius2-outTextHeight , getWriteTextPaint());
            canvas.rotate(90);
        }

        canvas.restore();


        /*八卦字*/
        for(int i=0;i<eightDiagramsText.length;i++){
            Paint p=null;
            if (i%2==0){
                p=getGreenTextPaint();
            }else{
                p=getWriteTextPaint();
            }
            offset = getTextOffSet(eightDiagramsText[i]);
            if (i==3){//第四个字体要反转 第六个也要
                canvas.save();
               // canvas.scale(0,-1);
                canvas.drawText(eightDiagramsText[i], 0 -offset.offSetX,-offset.offSetY-eightDiagramsRadius-outTextHeight , p);
                canvas.restore();

            }else{

                canvas.drawText(eightDiagramsText[i], 0 -offset.offSetX,-offset.offSetY-eightDiagramsRadius-outTextHeight , p);
            }

            canvas.rotate(45);
        }


        //中间九个小圆形
        for (Point p:numberPoints
             ) {
            canvas.drawCircle(p.x,p.y,numberRadius,getWriteFillPaint());
        }
        /*中间小圆的字 所有的字都会斜向上偏移，所以这里转画布*/
        canvas.save();
        offset=getTextOffSet(middleNumber[0]);
        canvas.translate(-offset.offSetX,-offset.offSetY);

        for(int i=0;i<middleNumber.length;i++){

            canvas.drawText(middleNumber[i],numberPoints.get(i).x,numberPoints.get(i).y,getGreenTextMiniPaint());
        }
        canvas.restore();




    }

    private Paint writeFillPaint=null;
    private Paint getWriteFillPaint() {

        if (writeFillPaint == null) {
            writeFillPaint = new Paint();
            writeFillPaint.setAntiAlias(true);
            writeFillPaint.setColor(Color.WHITE);
            writeFillPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        }
        return writeFillPaint;
    }

    private TextOffset getTextOffSet(String s) {
        Rect bounds = new Rect();
        TextOffset offset = new TextOffset();
        getGreenTextPaint().getTextBounds(s, 0, s.length(), bounds);
        offset.offSetX = (bounds.right + bounds.left) / 2;
        offset.offSetY = (bounds.top + bounds.bottom) / 2;
        return offset;
    }

    /**
     * 第一层画图
     *
     * @param canvas
     */
    private void drawLevelOne(Canvas canvas) {

        // 将坐标系原点移动到画布正中心
        canvas.translate(mWidth / 2, mHeight / 2);
        /*最里面的实体圆型*/
        canvas.drawCircle(0 , 0, centerRadius, getGreenFillPaint());

        /*实体原型外面的一个勾勒圆形*/
        canvas.drawCircle(0, 0, centerRadius2, getGreenStrokePaint());


        /*根据点集合来画一个path*/
        /*画第一个八卦*/
        Path path = new Path();
        path.moveTo(pointsSecond.get(0).x, pointsSecond.get(0).y);
        for (Point point : pointsSecond) {
            path.lineTo(point.x, point.y);

        }
        path.close();
        canvas.drawPath(path, getGreenStrokePaint());

        /*画第二个八卦*/
        Path path2 = new Path();
        path2.moveTo(pointsThird.get(0).x, pointsThird.get(0).y);
        for (Point point : pointsThird) {
            path2.lineTo(point.x, point.y);

        }
        path2.close();
        canvas.drawPath(path2, getGreenStrokePaint());
        /*画八卦的向内连线*/

        Path path3 = new Path();

        for (int i = 0; i < pointsFirst.size(); i++) {
            path3.moveTo(pointsFirst.get(i).x, pointsFirst.get(i).y);
            path3.lineTo(pointsThird.get(i).x, pointsThird.get(i).y);
        }
        canvas.drawPath(path3, getGreenStrokePaint());

        /*画八卦填充部分 也就是四个填充*/

        Path p1=new Path();
        p1.moveTo(pointsSecond.get(0).x,pointsSecond.get(0).y);
        p1.lineTo(pointsThird.get(0).x,pointsThird.get(0).y);
        p1.lineTo(pointsThird.get(1).x,pointsThird.get(1).y);
        p1.lineTo(pointsSecond.get(1).x,pointsSecond.get(1).y);
        p1.close();
        p1.moveTo(pointsSecond.get(2).x,pointsSecond.get(2).y);
        p1.lineTo(pointsThird.get(2).x,pointsThird.get(2).y);
        p1.lineTo(pointsThird.get(3).x,pointsThird.get(3).y);
        p1.lineTo(pointsSecond.get(3).x,pointsSecond.get(3).y);
        p1.close();

        p1.moveTo(pointsSecond.get(4).x,pointsSecond.get(4).y);
        p1.lineTo(pointsThird.get(4).x,pointsThird.get(4).y);
        p1.lineTo(pointsThird.get(5).x,pointsThird.get(5).y);
        p1.lineTo(pointsSecond.get(5).x,pointsSecond.get(5).y);
        p1.close();

        p1.moveTo(pointsSecond.get(6).x,pointsSecond.get(6).y);
        p1.lineTo(pointsThird.get(6).x,pointsThird.get(6).y);
        p1.lineTo(pointsThird.get(7).x,pointsThird.get(7).y);
        p1.lineTo(pointsSecond.get(7).x,pointsSecond.get(7).y);
        p1.close();
        canvas.drawPath(p1,getGreenFillPaint());

        /*画最外围的方向底部填充*/




    }

    private double getDistance(Point point, Point point1) {
        int offsetX=Math.abs(point.x-point1.x);
        int offsetY=Math.abs(point.y-point1.y);
        int d=offsetX*offsetX+offsetY*offsetY;
        return Math.sqrt(d);
    }

    private Paint greenStrokePaint = null;

    private Paint getGreenStrokePaint() {
        if (greenStrokePaint == null) {
            greenStrokePaint = new Paint();
            greenStrokePaint.setAntiAlias(true);
            greenStrokePaint.setColor(greenColor);
            greenStrokePaint.setStyle(Paint.Style.STROKE);
        }
        return greenStrokePaint;
    }

    private Paint blackFillPaint = null;

    /**
     * 绿色填充型画笔
     *
     * @return
     */
    private Paint getBlackFillPaint() {
        if (blackFillPaint == null) {
            blackFillPaint = new Paint();
            blackFillPaint.setAntiAlias(true);
            blackFillPaint.setColor(Color.BLACK);
            blackFillPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        }
        return blackFillPaint;
    }

    private Paint greenTextPaint = null;

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @return
     */
    public int px2sp(float pxValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public int sp2px(float spValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 绿色填充型画笔
     *
     * @return
     */
    private Paint getGreenTextPaint() {
        if (greenTextPaint == null) {
            greenTextPaint = new Paint();
            greenTextPaint.setAntiAlias(true);
            greenTextPaint.setColor(greenColor);
            greenTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            greenTextPaint.setTextSize(textSize);
        }
        return greenTextPaint;
    }
    private Paint writeTextPaint = null;
    /**
     * baise填充型画笔
     *
     * @return
     */
    private Paint getWriteTextPaint() {
        if (writeTextPaint == null) {
            writeTextPaint = new Paint();
            writeTextPaint.setAntiAlias(true);
            writeTextPaint.setColor(Color.WHITE);
            writeTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            writeTextPaint.setTextSize(textSize);
        }
        return writeTextPaint;
    }


    private Paint greenTextMiniPain=null;
    /**
     * 绿色填充型画笔 xiao
     *
     * @return
     */
    private Paint getGreenTextMiniPaint() {
        if (greenTextMiniPain == null) {
            greenTextMiniPain = new Paint();
            greenTextMiniPain.setAntiAlias(true);
            greenTextMiniPain.setColor(greenColor);
            greenTextMiniPain.setStyle(Paint.Style.FILL_AND_STROKE);
            greenTextMiniPain.setTextSize(numberTextSize);
        }
        return greenTextMiniPain;
    }




    Paint blackTextPaint=null;
    /**
     * 黑色填充型画笔
     *
     * @return
     */
    private Paint getBlackTextPaint() {
        if (blackTextPaint == null) {
            blackTextPaint = new Paint();
            blackTextPaint.setAntiAlias(true);
            blackTextPaint.setColor(Color.BLACK);
            blackTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            blackTextPaint.setTextSize(textSize);
        }
        return blackTextPaint;
    }



    private Paint greenFillPaint = null;

    /**
     * 绿色填充型画笔
     *
     * @return
     */
    private Paint getGreenFillPaint() {
        if (greenFillPaint == null) {
            greenFillPaint = new Paint();
            greenFillPaint.setAntiAlias(true);
            greenFillPaint.setColor(greenColor);
            greenFillPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        }
        return greenFillPaint;
    }

    private int eightDiagramsHeight = 200;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        /*view 绘制是一个正方形，这个边长取外部设定的view的高宽的最小值*/
        eightDiagramsHeight = Math.min(w, h);
        Log.d("yeqinfu", "==========onSizeChanged==========" + eightDiagramsHeight);
        init();

    }
}
