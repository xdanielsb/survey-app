export enum LogLevel {
  DEBUG = 'debug',
  INFO = 'info',
  WARN = 'warn',
  ERROR = 'error',
}

class Logger {
  constructor(private level: LogLevel = LogLevel.DEBUG) {}

  private shouldLog(level: LogLevel): boolean {
    const levels = Object.values(LogLevel)
    return levels.indexOf(level) >= levels.indexOf(this.level)
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  debug(message: string, ...args: any[]) {
    if (this.shouldLog(LogLevel.DEBUG)) console.debug('[DEBUG]', message, ...args)
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  info(message: string, ...args: any[]) {
    if (this.shouldLog(LogLevel.INFO)) console.info('[INFO]', message, ...args)
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  warn(message: string, ...args: any[]) {
    if (this.shouldLog(LogLevel.WARN)) console.warn('[WARN]', message, ...args)
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  error(message: string, ...args: any[]) {
    if (this.shouldLog(LogLevel.ERROR)) console.error('[ERROR]', message, ...args)
  }
}

export const logger = new Logger(
  import.meta.env.MODE === 'production' ? LogLevel.ERROR : LogLevel.DEBUG,
)
