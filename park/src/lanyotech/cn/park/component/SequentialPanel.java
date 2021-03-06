package lanyotech.cn.park.component;

public interface SequentialPanel {
	void init(Object...objects);
	void next(Object...objects);
	void pre(Object...objects);
	void onNext(Object...objects);
	void onPre(Object...objects);
	void setPrePanel(SequentialPanel panel);
	void show();
	void hide();
}
