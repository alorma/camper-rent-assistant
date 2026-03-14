package com.alorma.camperchecks.icons.filled

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.alorma.camperchecks.icons.AppIcons

val AppIcons.Filled.Delete: ImageVector
  get() {
    if (_delete != null) {
      return _delete!!
    }
    _delete =
      ImageVector
        .Builder(
          name = "Delete",
          defaultWidth = 24.dp,
          defaultHeight = 24.dp,
          viewportWidth = 24f,
          viewportHeight = 24f,
        ).apply {
          path(
            fill = null,
            fillAlpha = 1.0f,
            stroke = SolidColor(Color.Black),
            strokeAlpha = 1.0f,
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
            strokeLineMiter = 1.0f,
            pathFillType = PathFillType.NonZero,
          ) {
            // Trash bin lid
            moveTo(3f, 6f)
            lineTo(21f, 6f)
          }
          path(
            fill = null,
            fillAlpha = 1.0f,
            stroke = SolidColor(Color.Black),
            strokeAlpha = 1.0f,
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
            strokeLineMiter = 1.0f,
            pathFillType = PathFillType.NonZero,
          ) {
            // Trash bin body
            moveTo(19f, 6f)
            lineTo(18.1f, 20.1f)
            cubicTo(18.0f, 21.2f, 17.1f, 22f, 16f, 22f)
            lineTo(8f, 22f)
            cubicTo(6.9f, 22f, 6.0f, 21.2f, 5.9f, 20.1f)
            lineTo(5f, 6f)
          }
          path(
            fill = null,
            fillAlpha = 1.0f,
            stroke = SolidColor(Color.Black),
            strokeAlpha = 1.0f,
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
            strokeLineMiter = 1.0f,
            pathFillType = PathFillType.NonZero,
          ) {
            // Handle
            moveTo(9f, 6f)
            lineTo(9f, 4f)
            cubicTo(9f, 3.4f, 9.4f, 3f, 10f, 3f)
            lineTo(14f, 3f)
            cubicTo(14.6f, 3f, 15f, 3.4f, 15f, 4f)
            lineTo(15f, 6f)
          }
        }.build()
    return _delete!!
  }

private var _delete: ImageVector? = null
