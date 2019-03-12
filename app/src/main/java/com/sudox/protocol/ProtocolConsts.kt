package com.sudox.protocol

import com.sudox.protocol.helpers.readSignaturePublicKey
import java.math.BigInteger
import java.security.spec.ECFieldFp
import java.security.spec.ECParameterSpec
import java.security.spec.ECPoint
import java.security.spec.EllipticCurve

internal val BASE64_REGEX = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?\$".toRegex()
internal const val SIGN_PUBLIC_KEY_BODY = "MHYwEAYHKoZIzj0CAQYFK4EEACIDYgAEflkmgol1o7GFRjjB72BBbqhsRSI1SwHK" +
        "/7357yJaEzwrBUt231AiPD2AG2MNaXr8SqDCUv3jbLzOB4+/bVkcimZVP2elvjsp/AdU1335LpuCufavSCrftkzD0MeiUBqc"

internal val ELLIPTIC_CURVE_FINITE_FIELD = BigInteger("39402006196394479212279040100143613805079739270465" +
        "446667948293404245721771496870329047266088258938001861606973112319")

internal val ELLIPTIC_CURVE_A = BigInteger("39402006196394479212279040100143613805079739270465" +
        "446667948293404245721771496870329047266088258938001861606973112316")

internal val ELLIPTIC_CURVE_B = BigInteger("27580193559959705877849011840389048093056905856361" +
        "568521428707301988689241309860865136260764883745107765439761230575")

internal val ELLIPTIC_CURVE_N = BigInteger("3940200619639447921227904010014361380507973927046544" +
        "6667946905279627659399113263569398956308152294913554433653942643")

internal val ELLIPTIC_CURVE_G = 1
internal val ELLIPTIC_POINT_GX = BigInteger("26247035095799689268623156744566981891852923491109" +
        "213387815615900925518854738050089022388053975719786650872476732087")

internal val ELLIPTIC_POINT_GY = BigInteger("83257109614890299855467512895201081792878530488613" +
        "15594709205902480503199884419224438643760392947333078086511627871")

internal val ELLIPTIC_POINT = ECPoint(ELLIPTIC_POINT_GX, ELLIPTIC_POINT_GY)
internal val ELLIPTIC_CURVE = EllipticCurve(ECFieldFp(ELLIPTIC_CURVE_FINITE_FIELD), ELLIPTIC_CURVE_A, ELLIPTIC_CURVE_B)
internal val ELLIPTIC_CURVE_PARAM = ECParameterSpec(ELLIPTIC_CURVE, ELLIPTIC_POINT, ELLIPTIC_CURVE_N, ELLIPTIC_CURVE_G)
internal val SIGN_PUBLIC_KEY by lazy { readSignaturePublicKey(SIGN_PUBLIC_KEY_BODY) }