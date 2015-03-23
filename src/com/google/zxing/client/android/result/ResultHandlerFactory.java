/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client.android.result;

import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;
import com.google.zxing.client.result.ResultParser;

import android.app.Activity;

/**
 * Manufactures Android-specific handlers based on the barcode content's type.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ResultHandlerFactory {
  private ResultHandlerFactory() {
  }

  public static ResultHandler makeResultHandler(Activity activity, Result rawResult) {
    ParsedResult result = parseResult(rawResult);
    ParsedResultType type = result.getType();
    if (type.equals(ParsedResultType.EMAIL_ADDRESS)) {
      return new EmailAddressResultHandler(activity, result);
    } else if (type.equals(ParsedResultType.URI)) {
      return new URIResultHandler(activity, result);
    } else if (type.equals(ParsedResultType.GEO)) {
      return new GeoResultHandler(activity, result);
    } else if (type.equals(ParsedResultType.TEL)) {
      return new TelResultHandler(activity, result);
    } else {
      // The URIResultHandler is the fallthrough for unsupported formats.
      return new URIResultHandler(activity, result);
    }
  }

  private static ParsedResult parseResult(Result rawResult) {
    return ResultParser.parseResult(rawResult);
  }
}
