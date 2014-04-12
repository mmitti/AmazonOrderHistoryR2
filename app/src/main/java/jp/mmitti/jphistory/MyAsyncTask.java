package jp.mmitti.jphistory;

import android.os.AsyncTask;
import android.os.Handler;

/**
 * Android標準のAsyncTaskがいちいち戻り値とか書かないといけないのがめんどくさいため作成。<BR>
 * だいたい似た構造にしてあるが、戻り値などは使えない<BR>
 * 呼ばれる順番は{@link #preProcess()}→{@link #preProcessOnUI()}→<BR>
 * {@link #doBackGround()}→{@link #onFinish()}→{@link #onFinishOnUI()}<BR>
 * の順番で呼ばれる。
 * @author mmitti
 * @see android.os.AsyncTask
 */
/*
 * ライセンス
 * 改変、再配布等は自由ですが著作権表示 (C)mmitti 2012-2014をお願いします
 */
public abstract class MyAsyncTask implements Runnable{
	/**
	 * Handlerを取得するためのMainActivity
	 */
	protected Handler mHandler;
	/**
	 * Thread
	 */
	protected Thread mThread;

	/**
	 * コンストラクター
	 * @param context
	 */
	public MyAsyncTask(final Handler handler){
		mHandler = handler;
		if(handler == null)throw new NullPointerException();
	}

	/**
	 * 実行中かを調べる
	 * @return 実行中ならtrue
	 * @see Thread#isAlive()
	 */
	public boolean isRunning(){
		if(mThread == null)
			return false;
		return mThread.isAlive();
	}

	/**
	 * スレッドをスタートさせる<BR>
	 * 実行中に呼ぶと例外が発生する
	 * @throws IllegalThreadStateException
	 */
	public void start(){
		if(isRunning())
			throw new IllegalThreadStateException("Thread already started");
		mThread = new Thread(this);
		mThread.setName("MyAsyncTask");
		mThread.start();
	}

	/**
	 * スレッドをスタートさせ,終わるまで待つ<BR>
	 * 実行中に呼ぶと例外が発生する
	 * @throws IllegalThreadStateException
	 */
	public void startSync(){
		start();
		try{
			mThread.join();
		}catch(InterruptedException e){
		}
	}

	/**
	 * 処理スレッド
	 */
	@Override
	public void run(){
		try{
			CanWaitRunnable preProcessOnUIRun = new CanWaitRunnable(){
				@Override
				public void process() throws InterruptedException{
					preProcessOnUI();
				}
			};
			CanWaitRunnable onFinishOnUIRun = new CanWaitRunnable(){
				@Override
				public void process() throws InterruptedException{
					onFinishOnUI();
				}
			};
			// 前処理
			preProcess();
			mHandler.post(preProcessOnUIRun);
			try{
				preProcessOnUIRun.join();
				if(preProcessOnUIRun.isInterrupted())
					return;// TODO 治す・以上終了時の処理を挟む
			}catch(InterruptedException e){
			}
			// 処理
			doBackGround();
			// 終了処理
			onFinish();
			mHandler.post(onFinishOnUIRun);

			try{
				onFinishOnUIRun.join();
				if(onFinishOnUIRun.isInterrupted())
					return;
			}catch(InterruptedException e){
			}
		}catch(InterruptedException e){

		}
	}

	/**
	 * 処理スレッド内で利用される。<BR>
	 * 名前の通り終了するまで待つことができる。
	 * @author Masashi
	 */
	private abstract class CanWaitRunnable implements Runnable{
		/**
		 * 終了したかどうかのフラグ
		 */
		private boolean mIsFinished = false;
		private boolean mIsInterrupted = false;

		@Override
		public void run(){
			try{
				process();
			}catch(InterruptedException e){
				mIsInterrupted = true;
			}
			mIsFinished = true;
		}

		/**
		 * 終了したかどうかを確認する
		 * @return　終了したらtrue
		 */
		public boolean isFinished(){
			return mIsFinished;
		}

		/**
		 * 処理中に割り込みが入ったかどうかを確認する
		 * @return 割り込み時にtrue
		 */
		public boolean isInterrupted(){
			return mIsInterrupted;
		}

		/**
		 * 処理内容を継承して書く
		 * @throws InterruptedException
		 *             処理を中断するときに投げる
		 */
		public abstract void process() throws InterruptedException;

		/**
		 * 終了するまで待つ
		 * @throws InterruptedException
		 */
		public void join() throws InterruptedException{
			while(true){
				Thread.sleep(100);
				if(mIsFinished)
					return;
			}
		}
	}

	/**
	 * 処理を始める前に処理スレッドから呼ばれる
	 * @throws InterruptedException
	 *             処理を中断するときに投げる
	 * @see #preProcessOnUI() preProcessOnUI(次に呼ばれるメソッド)
	 */
	protected void preProcess() throws InterruptedException{
	}

	/**
	 * 処理を始める前にUIスレッドから呼ばれる
	 * @throws InterruptedException
	 *             処理を中断するときに投げる
	 * @see #preProcess() preProcess(前に呼ばれるメソッド)
	 * @see #doBackGround() doBackGround(次に呼ばれるメソッド)
	 */
	protected void preProcessOnUI() throws InterruptedException{
	}

	/**
	 * バックグラウンドで行う処理を記述する
	 * @throws InterruptedException
	 *             処理を中断するときに投げる
	 * @see #preProcessOnUI() preProcessOnUI(前に呼ばれるメソッド)
	 * @see #onFinish() onFinish(次に呼ばれるメソッド)
	 */
	protected abstract void doBackGround() throws InterruptedException;

	/**
	 * 処理終了後に処理スレッドから呼ばれる
	 * @throws InterruptedException
	 *             処理を中断するときに投げる
	 * @see #doBackGround() doBackGround(前に呼ばれるメソッド)
	 * @see #onFinishOnUI() onFinishOnUI(次に呼ばれるメソッド)
	 */
	protected void onFinish() throws InterruptedException{
	}

	/**
	 * 処理終了後にUIスレッドから呼ばれる
	 * @throws InterruptedException
	 *             処理を中断するときに投げる
	 * @see #onFinish() onFinish(前に呼ばれるメソッド)
	 */
	protected void onFinishOnUI() throws InterruptedException{
	}

}
