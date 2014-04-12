package jp.mmitti.jphistory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


/**
 * {@link TypeReference}を参考に作成<BR>
 * 利用時は継承するなり匿名クラスなりにして使う
 * @author Masashi
 * @param <T>
 */
public abstract class TypeRef<T> implements Type{
	/**
	 * Objectを指定された型にキャストする
	 * @param o
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T convertData(final Object o){
		return (T) o;
	}


	/**
	 * {@link TypeReference}を参考に（ほぼ引用）作成<BR>
	 * {@link java.lang.reflect.Type}を返す
	 * @return
	 */
	public Type getType(){
		Type type = this.getClass().getGenericSuperclass();
		if(type instanceof ParameterizedType){
			Type[] args = ((ParameterizedType) type).getActualTypeArguments();
			if(args != null && args.length == 1){
				return args[0];
			}
		}
		throw new IllegalStateException("Reference must be specified actual type.");
	}

	/**
	 * 内部のTypeを文字列にする<BR>
	 * {@link TypeReference}から引用
	 */
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder(getClass().getSimpleName());
		sb.append("[").append(getType()).append("]");
		return sb.toString();
	}
}
