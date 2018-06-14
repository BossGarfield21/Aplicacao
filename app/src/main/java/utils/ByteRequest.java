package utils;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

public class ByteRequest extends Request<byte[]> {



    private final Response.Listener<byte[]> mListener;
    public ByteRequest(int method, String url, Response.Listener<byte[]> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = listener;
    }

    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        try {
            return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e){
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(byte[] response) {
        mListener.onResponse(response);
    }
}
