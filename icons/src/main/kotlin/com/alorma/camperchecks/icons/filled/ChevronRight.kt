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

val AppIcons.Filled.ChevronRight: ImageVector
  get() {
    if (_chevronRight != null) {
      return _chevronRight!!
    }
    _chevronRight =
      ImageVector
        .Builder(
          name = "ChevronRight",
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
            // Arrow pointing right
            moveTo(9f, 18f)
            lineTo(15f, 12f)
            lineTo(9f, 6f)
          }
        }.build()
    return _chevronRight!!
  }

private var _chevronRight: ImageVector? = null
