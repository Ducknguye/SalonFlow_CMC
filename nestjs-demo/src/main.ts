import 'reflect-metadata';
import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import helmet from 'helmet';
import cookieParser from 'cookie-parser';
import csurf from 'csurf';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);

  app.use(helmet());
  app.use(cookieParser());
  // csurf using cookie-based tokens; forms should include the token as _csrf or header
  app.use(csurf({ cookie: true }) as any);

  await app.listen(3000);
  console.log('NestJS demo listening on http://localhost:3000');
}

if (require.main === module) {
  bootstrap();
}

export { bootstrap };
