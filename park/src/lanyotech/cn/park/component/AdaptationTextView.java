package lanyotech.cn.park.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

public class AdaptationTextView extends TextView {

	private float trueWidth;
	private float trueHeight;
	
	private boolean isEnglish;
	
	public AdaptationTextView(Context context) {
		super(context);
	}

	public AdaptationTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public AdaptationTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	
	public void adaptation(){
	//	isEnglish=SystemConfigServiceImpl.getInstance().getCurrentLocale().equals("en_us");
		isEnglish=getContext().getResources().getConfiguration().locale.getLanguage().equals("en");
		trueWidth=getWidth()-getPaddingLeft()-getPaddingRight();
		trueHeight=getHeight()-getPaddingTop()-getPaddingBottom();
		CharSequence text= getText();
		while(isEnglish?adaptationCheck(text.toString().split(" ")):adaptationCheck(text)){
			float size=getPaint().getTextSize()-1;
			if(size<=0){
				break;
			}else{
		//		getPaint().setTextSize(size);
				setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
			}
		}
		setText(text);
	}
	
	public boolean adaptationCheck(CharSequence text){
		float textWidth= getPaint().measureText(text, 0, text.length());
		/*System.out.println("left:"+getPaddingLeft()+","+"right:"+getPaddingRight());
		System.out.println("textWidth:"+textWidth+",trueWidth"+trueWidth);
		System.out.println("LineHeight:"+getLineHeight()+",trueHeight"+trueHeight);
		System.out.println("width:"+getWidth());
		System.out.println("left-right:"+getLeft()+","+getRight());
		System.out.println("size:"+getPaint().getTextSize());*/
		if(trueWidth<textWidth){
			if(trueHeight>getLineHeight()){
				float currentWidth=textWidth,currentHeight=trueHeight;
				int start=0;
				while(true){
					float tempTextWidth=0;
					float charWidth=0;
					while(start<text.length()&&(charWidth= getPaint().measureText(text, start, start+1))+tempTextWidth<trueWidth){
						start++;
						tempTextWidth+=charWidth;
					}
					currentWidth-=tempTextWidth;
					if(currentWidth>0){
						currentHeight-=getLineHeight();
						if(currentHeight<getLineHeight()){
							return true;
						}
					}else{
						return false;
					}
					
				}
			}
		}
		return false;
	}
	
	public boolean adaptationCheck(String[] texts){
		if(texts.length<1)
			return false;
		Paint p=getPaint();
		float spaceWidth=p.measureText(" ");
		float textWidth= p.measureText(getText(), 0, getText().length());
		if(trueWidth<textWidth){
			if(trueHeight>getLineHeight()){
				float currentWidth=trueWidth,currentHeight=trueHeight;
				float tempTextWidth=0;
				for(int i=0;i<texts.length;i++){
					tempTextWidth=p.measureText(texts[0]);
					if(currentWidth<trueWidth){
						if(currentWidth<tempTextWidth+spaceWidth){
							currentHeight-=getLineHeight();
							currentWidth=trueWidth;
						}else{
							currentWidth-=tempTextWidth+spaceWidth;
							continue;
						}
					}
					int needRow=(int) (tempTextWidth/trueWidth+(tempTextWidth%trueWidth==0?0:1));
					if(currentHeight<getLineHeight()*needRow){
						return true;
					}
					currentHeight-=getLineHeight()*(needRow-1);
					currentWidth-=tempTextWidth-(trueWidth*(needRow-1));
				}
				
				
				/*int start=0;
				while(true){
					float tempTextWidth=0;
					float charWidth=0;
					while(start<text.length()&&(charWidth= getPaint().measureText(text, start, start+1))+tempTextWidth<trueWidth){
						start++;
						tempTextWidth+=charWidth;
					}
					currentWidth-=tempTextWidth;
					if(currentWidth>0){
						currentHeight-=getLineHeight();
						System.out.println("cs!!!!!!!!!:"+currentWidth+","+getLineHeight());
						if(currentHeight<getLineHeight()){
							return true;
						}
					}else{
						return false;
					}
					
				}*/
			}
		}
		return false;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		adaptation();
	}

	
	
	
}
